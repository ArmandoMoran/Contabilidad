package com.contabilidad.invoicing;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CancelRequest(
    @NotBlank String reasonCode,
    UUID replacementUuid
) {}
