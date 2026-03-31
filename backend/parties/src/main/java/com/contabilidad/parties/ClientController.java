package com.contabilidad.parties;

import com.contabilidad.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public PageResponse<ClientDto> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            Pageable pageable) {
        Page<Client> page = clientService.listClients(companyId, pageable);
        return PageResponse.of(
            page.getContent().stream().map(ClientMapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto create(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody CreateClientRequest request) {
        Client client = clientService.createClient(companyId, request);
        return ClientMapper.toDto(client);
    }

    @GetMapping("/{id}")
    public ClientDto get(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        Client client = clientService.getClient(companyId, id);
        return ClientMapper.toDto(client);
    }

    @PatchMapping("/{id}")
    public ClientDto update(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClientRequest request) {
        Client client = clientService.updateClient(companyId, id, request);
        return ClientMapper.toDto(client);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        clientService.deleteClient(companyId, id, userId);
    }
}
