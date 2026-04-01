package com.contabilidad.parties;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public PageResponse<ClientDto> list(Pageable pageable) {
        var companyId = SecurityContextUtils.currentCompanyId();
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
    public ClientDto create(@Valid @RequestBody CreateClientRequest request) {
        Client client = clientService.createClient(SecurityContextUtils.currentCompanyId(), request);
        return ClientMapper.toDto(client);
    }

    @GetMapping("/{id}")
    public ClientDto get(@PathVariable java.util.UUID id) {
        Client client = clientService.getClient(SecurityContextUtils.currentCompanyId(), id);
        return ClientMapper.toDto(client);
    }

    @PatchMapping("/{id}")
    public ClientDto update(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody UpdateClientRequest request) {
        Client client = clientService.updateClient(SecurityContextUtils.currentCompanyId(), id, request);
        return ClientMapper.toDto(client);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable java.util.UUID id) {
        clientService.deleteClient(SecurityContextUtils.currentCompanyId(), id, SecurityContextUtils.currentUserId());
    }
}
