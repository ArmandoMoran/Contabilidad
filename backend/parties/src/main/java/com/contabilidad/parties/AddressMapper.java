package com.contabilidad.parties;

import java.util.UUID;

public final class AddressMapper {

    private AddressMapper() {}

    public static AddressDto toDto(Address a) {
        return new AddressDto(
            a.getId(),
            a.getCompanyId(),
            a.getPartyType(),
            a.getPartyId(),
            a.getAddressType(),
            a.getStreet1(),
            a.getStreet2(),
            a.getExteriorNumber(),
            a.getInteriorNumber(),
            a.getNeighborhood(),
            a.getCity(),
            a.getMunicipalityCode(),
            a.getStateCode(),
            a.getPostalCode(),
            a.getCountryCode(),
            a.isPrimary(),
            a.getCreatedAt(),
            a.getUpdatedAt()
        );
    }

    public static Address toEntity(UUID companyId, String partyType, UUID partyId, CreateAddressRequest r) {
        Address a = new Address();
        a.setCompanyId(companyId);
        a.setPartyType(partyType);
        a.setPartyId(partyId);
        a.setAddressType(r.addressType() != null ? r.addressType() : "FISCAL");
        a.setStreet1(r.street1());
        a.setStreet2(r.street2());
        a.setExteriorNumber(r.exteriorNumber());
        a.setInteriorNumber(r.interiorNumber());
        a.setNeighborhood(r.neighborhood());
        a.setCity(r.city());
        a.setMunicipalityCode(r.municipalityCode());
        a.setStateCode(r.stateCode());
        a.setPostalCode(r.postalCode());
        a.setCountryCode(r.countryCode() != null ? r.countryCode() : "MEX");
        a.setPrimary(r.isPrimary());
        return a;
    }
}
