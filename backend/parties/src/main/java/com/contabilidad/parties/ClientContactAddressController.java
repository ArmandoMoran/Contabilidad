package com.contabilidad.parties;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}")
public class ClientContactAddressController {

    private static final Logger log = LoggerFactory.getLogger(ClientContactAddressController.class);

    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    public ClientContactAddressController(ContactRepository contactRepository,
                                          AddressRepository addressRepository) {
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
    }

    // ── Contacts ──

    @GetMapping("/contacts")
    public List<ContactDto> listContacts(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID clientId) {
        log.info("Listing contacts for clientId={}", clientId);
        return contactRepository.findByPartyTypeAndPartyIdAndDeletedAtIsNull("CLIENT", clientId)
                .stream().map(ContactMapper::toDto).toList();
    }

    @PostMapping("/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactDto createContact(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID clientId,
            @Valid @RequestBody CreateContactRequest request) {
        log.info("Creating contact for clientId={}, name={}", clientId, request.fullName());
        Contact contact = ContactMapper.toEntity(companyId, "CLIENT", clientId, request);
        contact = contactRepository.save(contact);
        log.info("Created contact id={}", contact.getId());
        return ContactMapper.toDto(contact);
    }

    @DeleteMapping("/contacts/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID clientId,
            @PathVariable UUID contactId) {
        log.info("Deleting contact id={} for clientId={}", contactId, clientId);
        contactRepository.findById(contactId).ifPresent(c -> {
            c.setDeletedAt(Instant.now());
            contactRepository.save(c);
        });
    }

    // ── Addresses ──

    @GetMapping("/addresses")
    public List<AddressDto> listAddresses(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID clientId) {
        log.info("Listing addresses for clientId={}", clientId);
        return addressRepository.findByPartyTypeAndPartyIdAndDeletedAtIsNull("CLIENT", clientId)
                .stream().map(AddressMapper::toDto).toList();
    }

    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto createAddress(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID clientId,
            @Valid @RequestBody CreateAddressRequest request) {
        log.info("Creating address for clientId={}, postal={}", clientId, request.postalCode());
        Address address = AddressMapper.toEntity(companyId, "CLIENT", clientId, request);
        address = addressRepository.save(address);
        log.info("Created address id={}", address.getId());
        return AddressMapper.toDto(address);
    }

    @DeleteMapping("/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID clientId,
            @PathVariable UUID addressId) {
        log.info("Deleting address id={} for clientId={}", addressId, clientId);
        addressRepository.findById(addressId).ifPresent(a -> {
            a.setDeletedAt(Instant.now());
            addressRepository.save(a);
        });
    }
}
