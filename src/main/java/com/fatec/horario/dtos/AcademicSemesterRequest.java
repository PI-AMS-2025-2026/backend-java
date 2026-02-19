package com.fatec.horario.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AcademicSemesterRequest (

    @NotNull(message = "Academic Year is required")
    @Min(value = 2000, message = "Academic Year must be greater than or equal to 2000")
    @Max(value = 2100, message = "Academic Year must be less than or equal to 3000")
    Integer academicYear,

    @NotBlank(message = "Status is required")
    @Size(min = 3, max = 64, message = "Status must be between 3 and 64 characters")
    String status,
    Long courseId
) {}
