package com.fatec.horario.dto.ClassGroup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ClassGroupRequest(

        @NotNull(message = "Student Count is required") @Positive(message = "Student Count must positive") Integer studentCount) {

}
