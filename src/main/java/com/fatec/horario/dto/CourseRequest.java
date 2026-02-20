package com.fatec.horario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequest(
                @NotBlank(message = "Name is required") @Size(min = 2, max = 255, message = "the name must be between 2 and 255 characters long") String name,

                @NotBlank(message = "description is required") @Size(min = 2, max = 255, message = "the description must be between 2 and 255 characters long") String description,

                Long modalityId,
                Long periodicityId

) {

}
