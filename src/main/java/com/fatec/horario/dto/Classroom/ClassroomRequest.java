package com.fatec.horario.dto.Classroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ClassroomRequest(

    @NotBlank(message = "Name is required.") @Size(min = 2, max = 255, message = "the name must be between 2 and 255 characters long.") String name,

    @Size(min = 2, max = 255, message = "Location must be between 2 and 255 characters long.") String location,

    @NotBlank(message = "PhysicalResources is required.") @Size(min = 2, max = 255, message = "PhysicalResources must be between 2 and 255 characters long.") String physicalResources,

    @NotBlank(message = "SoftwareResources is required.") @Size(min = 2, max = 255, message = "SoftwareResources must be between 2 and 255 characters long.") String softwareResources,

    @NotNull(message = "Capacity is required.") @Positive(message = "Capacity must be positive.") Integer capacity,

    @NotNull(message = "Template is required.") Boolean template,

    @NotNull(message = "Practical is required.") Boolean practical

) {

}
