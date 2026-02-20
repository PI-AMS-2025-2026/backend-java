package com.fatec.horario.dto.Subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
                @NotBlank(message = "Name is required") @Size(max = 100) String name,
                @Size(max = 20) String acronym,
                Long techAxisId,
                Long modalityId) {

}
