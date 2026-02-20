package com.fatec.horario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must have at most 100 characters") String name,

        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 120) String email,

        String password,
        Long accessLevelId) {
}
