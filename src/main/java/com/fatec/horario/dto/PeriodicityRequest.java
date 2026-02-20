package com.fatec.horario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PeriodicityRequest(
        @NotBlank(message = "Description is required") @Size(max = 255) String description) {
}
