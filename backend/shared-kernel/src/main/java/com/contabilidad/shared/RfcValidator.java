package com.contabilidad.shared;

public class RfcValidator {

    private static final String RFC_PM_PATTERN = "^[A-ZÑ&]{3}\\d{6}[A-Z0-9]{3}$";
    private static final String RFC_PF_PATTERN = "^[A-ZÑ&]{4}\\d{6}[A-Z0-9]{3}$";
    private static final String RFC_GENERIC_NACIONAL = "XAXX010101000";
    private static final String RFC_GENERIC_EXTRANJERO = "XEXX010101000";

    private RfcValidator() {}

    public static boolean isValid(String rfc) {
        if (rfc == null || rfc.isBlank()) return false;
        String upper = rfc.toUpperCase().trim();
        if (upper.equals(RFC_GENERIC_NACIONAL) || upper.equals(RFC_GENERIC_EXTRANJERO)) return true;
        return upper.matches(RFC_PM_PATTERN) || upper.matches(RFC_PF_PATTERN);
    }

    public static boolean isPersonaMoral(String rfc) {
        if (rfc == null) return false;
        return rfc.trim().length() == 12;
    }

    public static boolean isPersonaFisica(String rfc) {
        if (rfc == null) return false;
        String trimmed = rfc.trim().toUpperCase();
        return trimmed.length() == 13
            && !trimmed.equals(RFC_GENERIC_NACIONAL)
            && !trimmed.equals(RFC_GENERIC_EXTRANJERO);
    }

    public static boolean isGeneric(String rfc) {
        if (rfc == null) return false;
        String upper = rfc.toUpperCase().trim();
        return upper.equals(RFC_GENERIC_NACIONAL) || upper.equals(RFC_GENERIC_EXTRANJERO);
    }
}
