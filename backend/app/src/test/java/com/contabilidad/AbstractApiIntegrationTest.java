package com.contabilidad;

import com.contabilidad.identity.User;
import com.contabilidad.identity.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractApiIntegrationTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("contabilidad_test")
        .withUsername("contabilidad")
        .withPassword("contabilidad_dev");

    static {
        postgres.start();
    }

    @LocalServerPort
    protected int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected HttpClient httpClient;
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
        registry.add("spring.flyway.clean-disabled", () -> false);
    }

    @BeforeEach
    void setUpHttpClient() {
        httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
        ensureLoginUser();
    }

    protected HttpResponse<String> get(String path) throws IOException, InterruptedException {
        return send("GET", path, null, null);
    }

    protected HttpResponse<String> get(String path, String accessToken) throws IOException, InterruptedException {
        return send("GET", path, null, accessToken);
    }

    protected HttpResponse<String> post(String path, Object body) throws IOException, InterruptedException {
        return send("POST", path, body, null);
    }

    protected HttpResponse<String> post(String path, Object body, String accessToken) throws IOException, InterruptedException {
        return send("POST", path, body, accessToken);
    }

    protected JsonNode jsonBody(HttpResponse<String> response) throws IOException {
        return objectMapper.readTree(response.body());
    }

    protected String loginAndGetAccessToken() throws IOException, InterruptedException {
        HttpResponse<String> response = post("/auth/login", Map.of(
            "email", "admin@demo.com",
            "password", "demo1234"
        ));

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Login failed with status " + response.statusCode() + ": " + response.body());
        }

        return jsonBody(response).path("accessToken").asText();
    }

    private void ensureLoginUser() {
        User user = userRepository.findByEmail("admin@demo.com").orElseGet(User::new);
        user.setId(UUID.fromString("22222222-2222-7222-8222-222222222222"));
        user.setCompanyId(UUID.fromString("11111111-1111-7111-8111-111111111111"));
        user.setEmail("admin@demo.com");
        user.setFullName("Admin Demo");
        user.setRole("admin");
        user.setActive(true);
        user.setLocked(false);
        user.setFailedAttempts(0);
        user.setPasswordHash(passwordEncoder.encode("demo1234"));
        userRepository.save(user);
    }

    private HttpResponse<String> send(String method, String path, Object body, String accessToken)
            throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/v1" + path))
            .timeout(Duration.ofSeconds(15))
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (accessToken != null) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }

        if (body == null) {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            requestBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        }

        return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
