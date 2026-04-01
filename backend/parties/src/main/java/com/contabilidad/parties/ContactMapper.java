package com.contabilidad.parties;

import java.util.UUID;

public final class ContactMapper {

    private ContactMapper() {}

    public static ContactDto toDto(Contact c) {
        return new ContactDto(
            c.getId(),
            c.getCompanyId(),
            c.getPartyType(),
            c.getPartyId(),
            c.getFullName(),
            c.getEmail(),
            c.getPhone(),
            c.getPosition(),
            c.isPrimary(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }

    public static Contact toEntity(UUID companyId, String partyType, UUID partyId, CreateContactRequest r) {
        Contact c = new Contact();
        c.setCompanyId(companyId);
        c.setPartyType(partyType);
        c.setPartyId(partyId);
        c.setFullName(r.fullName());
        c.setEmail(r.email());
        c.setPhone(r.phone());
        c.setPosition(r.position());
        c.setPrimary(r.isPrimary());
        return c;
    }
}
