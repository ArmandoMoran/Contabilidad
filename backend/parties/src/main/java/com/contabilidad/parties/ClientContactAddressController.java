package com.contabilidad.parties;

import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
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
    public List<ContactDto> listContacts(@PathVariable java.util.UUID clientId) {
        log.info("Listing contacts for clientId={}", clientId);
        return contactRepository.findByPartyTypeAndPartyIdAndDeletedAtIsNull("CLIENT", clientId)
                .stream().map(ContactMapper::toDto).toList();
    }

    @PostMapping("/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactDto createContact(
            @PathVariable java.util.UUID clientId,
            @Valid @RequestBody CreateContactRequest request) {
        log.info("Creating contact for clientId={}, name={}", clientId, request.fullName());
        Contact contact = ContactMapper.toEntity(SecurityContextUtils.currentCompanyId(), "CLIENT", clientId, request);
        contact = contactRepository.save(contact);
        log.info("Created contact id={}", contact.getId());
        return ContactMapper.toDto(contact);
    }

    @DeleteMapping("/contacts/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(
            @PathVariable java.util.UUID clientId,
            @PathVariable java.util.UUID contactId) {
        log.info("Deleting contact id={} for clientId={}", contactId, clientId);
        contactRepository.findById(contactId).ifPresent(c -> {
            c.setDeletedAt(Instant.now());
            contactRepository.save(c);
        });
    }

    // ── Addresses ──

    @GetMapping("/addresses")
    public List<AddressDto> listAddresses(@PathVariable java.util.UUID clientId) {
        log.info("Listing addresses for clientId={}", clientId);
        return addressRepository.findByPartyTypeAndPartyIdAndDeletedAtIsNull("CLIENT", clientId)
                .stream().map(AddressMapper::toDto).toList();
    }

    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto createAddress(
            @PathVariable java.util.UUID clientId,
            @Valid @RequestBody CreateAddressRequest request) {
        log.info("Creating address for clientId={}, postal={}", clientId, request.postalCode());
        Address address = AddressMapper.toEntity(SecurityContextUtils.currentCompanyId(), "CLIENT", clientId, request);
        address = addressRepository.save(address);
        log.info("Created address id={}", address.getId());
        return AddressMapper.toDto(address);
    }

    @DeleteMapping("/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @PathVariable java.util.UUID clientId,
            @PathVariable java.util.UUID addressId) {
        log.info("Deleting address id={} for clientId={}", addressId, clientId);
        addressRepository.findById(addressId).ifPresent(a -> {
            a.setDeletedAt(Instant.now());
            addressRepository.save(a);
        });
    }
}
