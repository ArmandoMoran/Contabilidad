package com.contabilidad.parties;

import com.contabilidad.shared.RfcValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public Page<Client> listClients(UUID companyId, String search, Pageable pageable) {
        log.info("Listing clients for companyId={}, search={}, page={}", companyId, search, pageable);
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        Page<Client> page = normalizedSearch == null
            ? clientRepository.findByCompanyIdAndDeletedAtIsNull(companyId, pageable)
            : clientRepository.searchByCompanyId(companyId, normalizedSearch, pageable);
        log.debug("Found {} clients (total={})", page.getNumberOfElements(), page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public Client getClient(UUID companyId, UUID clientId) {
        log.info("Getting client companyId={}, clientId={}", companyId, clientId);
        return clientRepository.findByCompanyIdAndIdAndDeletedAtIsNull(companyId, clientId)
            .orElseThrow(() -> {
                log.warn("Client not found: companyId={}, clientId={}", companyId, clientId);
                return new EntityNotFoundException("Client not found: " + clientId);
            });
    }

    public Client createClient(UUID companyId, CreateClientRequest request) {
        log.info("Creating client companyId={}, rfc={}", companyId, request.rfc());
        if (!RfcValidator.isValid(request.rfc())) {
            log.warn("Invalid RFC: {}", request.rfc());
            throw new IllegalArgumentException("Invalid RFC: " + request.rfc());
        }
        if (clientRepository.existsByCompanyIdAndRfc(companyId, request.rfc().toUpperCase().trim())) {
            log.warn("Duplicate RFC: {}", request.rfc());
            throw new DuplicateRfcException(request.rfc());
        }
        Client client = ClientMapper.toEntity(companyId, request);
        client = clientRepository.save(client);
        log.info("Created client id={}", client.getId());
        return client;
    }

    public Client updateClient(UUID companyId, UUID clientId, UpdateClientRequest request) {
        Client client = getClient(companyId, clientId);

        if (request.rfc() != null) {
            if (!RfcValidator.isValid(request.rfc())) {
                throw new IllegalArgumentException("Invalid RFC: " + request.rfc());
            }
            String newRfc = request.rfc().toUpperCase().trim();
            if (!newRfc.equals(client.getRfc()) && clientRepository.existsByCompanyIdAndRfc(companyId, newRfc)) {
                throw new DuplicateRfcException(request.rfc());
            }
            client.setRfc(newRfc);
        }
        if (request.legalName() != null) client.setLegalName(request.legalName());
        if (request.tradeName() != null) client.setTradeName(request.tradeName());
        if (request.email() != null) client.setEmail(request.email());
        if (request.phone() != null) client.setPhone(request.phone());
        if (request.website() != null) client.setWebsite(request.website());
        if (request.fiscalRegimeCode() != null) client.setFiscalRegimeCode(request.fiscalRegimeCode());
        if (request.defaultUsoCfdiCode() != null) client.setDefaultUsoCfdiCode(request.defaultUsoCfdiCode());
        if (request.defaultFormaPagoCode() != null) client.setDefaultFormaPagoCode(request.defaultFormaPagoCode());
        if (request.defaultMetodoPagoCode() != null) client.setDefaultMetodoPagoCode(request.defaultMetodoPagoCode());
        if (request.defaultPostalCode() != null) client.setDefaultPostalCode(request.defaultPostalCode());
        if (request.notes() != null) client.setNotes(request.notes());
        if (request.active() != null) client.setActive(request.active());

        return clientRepository.save(client);
    }

    public void deleteClient(UUID companyId, UUID clientId, UUID userId) {
        Client client = getClient(companyId, clientId);
        client.softDelete(userId);
        clientRepository.save(client);
    }
}
