package com.contabilidad.parties;

import com.contabilidad.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "party_type", nullable = false, length = 10)
    private String partyType;

    @Column(name = "party_id", nullable = false)
    private UUID partyId;

    @Column(name = "address_type", nullable = false, length = 20)
    private String addressType = "FISCAL";

    @Column(name = "street1")
    private String street1;

    @Column(name = "street2")
    private String street2;

    @Column(name = "exterior_number")
    private String exteriorNumber;

    @Column(name = "interior_number")
    private String interiorNumber;

    @Column(name = "neighborhood")
    private String neighborhood;

    @Column(name = "city")
    private String city;

    @Column(name = "municipality_code", length = 5)
    private String municipalityCode;

    @Column(name = "state_code", length = 5)
    private String stateCode;

    @Column(name = "postal_code", length = 5)
    private String postalCode;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode = "MEX";

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Address() {}

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getPartyType() { return partyType; }
    public void setPartyType(String partyType) { this.partyType = partyType; }

    public UUID getPartyId() { return partyId; }
    public void setPartyId(UUID partyId) { this.partyId = partyId; }

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }

    public String getStreet1() { return street1; }
    public void setStreet1(String street1) { this.street1 = street1; }

    public String getStreet2() { return street2; }
    public void setStreet2(String street2) { this.street2 = street2; }

    public String getExteriorNumber() { return exteriorNumber; }
    public void setExteriorNumber(String exteriorNumber) { this.exteriorNumber = exteriorNumber; }

    public String getInteriorNumber() { return interiorNumber; }
    public void setInteriorNumber(String interiorNumber) { this.interiorNumber = interiorNumber; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getMunicipalityCode() { return municipalityCode; }
    public void setMunicipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
