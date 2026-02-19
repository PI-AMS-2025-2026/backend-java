package com.fatec.horario.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModalityRequest(
        @NotBlank(message = "Name is required") 
        @Size(max = 100, message = "Name must be at most 100 characters long") 
        String name
) {

}
