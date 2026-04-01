package com.contabilidad.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RfcValidatorTest {

    // --- isValid ---

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void isValid_rejectsNullBlankEmpty(String rfc) {
        assertThat(RfcValidator.isValid(rfc)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ABC123456XY9",   // persona moral (12 chars)
        "ABCD123456XY9",  // persona fisica (13 chars)
        "A&C123456XY9",   // ampersand allowed
        "AÑC123456XY9"    // Ñ allowed
    })
    void isValid_acceptsValidRfcs(String rfc) {
        assertThat(RfcValidator.isValid(rfc)).isTrue();
    }

    @Test
    void isValid_acceptsGenericNacional() {
        assertThat(RfcValidator.isValid("XAXX010101000")).isTrue();
    }

    @Test
    void isValid_acceptsGenericExtranjero() {
        assertThat(RfcValidator.isValid("XEXX010101000")).isTrue();
    }

    @Test
    void isValid_isCaseInsensitive() {
        assertThat(RfcValidator.isValid("xaxx010101000")).isTrue();
        assertThat(RfcValidator.isValid("abc123456xy9")).isTrue();
    }

    @Test
    void isValid_trimsWhitespace() {
        assertThat(RfcValidator.isValid("  ABC123456XY9  ")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SHORT",
        "TOOLONGSTRING1234",
        "123456789012",     // all digits, 12 chars
        "ABCDEFGHIJKLM"     // no digits in date portion
    })
    void isValid_rejectsInvalidFormats(String rfc) {
        assertThat(RfcValidator.isValid(rfc)).isFalse();
    }

    // --- isPersonaMoral ---

    @Test
    void isPersonaMoral_trueFor12Chars() {
        assertThat(RfcValidator.isPersonaMoral("ABC123456XY9")).isTrue();
    }

    @Test
    void isPersonaMoral_falseFor13Chars() {
        assertThat(RfcValidator.isPersonaMoral("ABCD123456XY9")).isFalse();
    }

    @Test
    void isPersonaMoral_falseForNull() {
        assertThat(RfcValidator.isPersonaMoral(null)).isFalse();
    }

    // --- isPersonaFisica ---

    @Test
    void isPersonaFisica_trueFor13CharNonGeneric() {
        assertThat(RfcValidator.isPersonaFisica("ABCD123456XY9")).isTrue();
    }

    @Test
    void isPersonaFisica_falseForGenericNacional() {
        assertThat(RfcValidator.isPersonaFisica("XAXX010101000")).isFalse();
    }

    @Test
    void isPersonaFisica_falseForGenericExtranjero() {
        assertThat(RfcValidator.isPersonaFisica("XEXX010101000")).isFalse();
    }

    @Test
    void isPersonaFisica_falseFor12Chars() {
        assertThat(RfcValidator.isPersonaFisica("ABC123456XY9")).isFalse();
    }

    @Test
    void isPersonaFisica_falseForNull() {
        assertThat(RfcValidator.isPersonaFisica(null)).isFalse();
    }

    // --- isGeneric ---

    @Test
    void isGeneric_trueForNacional() {
        assertThat(RfcValidator.isGeneric("XAXX010101000")).isTrue();
    }

    @Test
    void isGeneric_trueForExtranjero() {
        assertThat(RfcValidator.isGeneric("XEXX010101000")).isTrue();
    }

    @Test
    void isGeneric_isCaseInsensitive() {
        assertThat(RfcValidator.isGeneric("xaxx010101000")).isTrue();
    }

    @Test
    void isGeneric_falseForRegularRfc() {
        assertThat(RfcValidator.isGeneric("ABC123456XY9")).isFalse();
    }

    @Test
    void isGeneric_falseForNull() {
        assertThat(RfcValidator.isGeneric(null)).isFalse();
    }
}