package com.fatec.horario.dto.TechAxis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TechAxisRequest(

        @NotBlank(message = "Axis Name is required") @Size(min = 3, max = 100, message = "Axis Name must be between 3 and 100 characters") String name

) {
}
