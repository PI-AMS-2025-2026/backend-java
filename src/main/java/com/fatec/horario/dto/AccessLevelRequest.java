package com.fatec.horario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AccessLevelRequest(

        @NotNull(message = "Level is required.") @Positive(message = "Level must be positive.") Integer level,

        @NotBlank(message = "Name is required") @Size(min = 2, max = 255, message = "Description must be between 2 and 255 characters long") String description

) {

}
