package com.fatec.horario.mappers;

import com.fatec.horario.dtos.ClassroomRequest;
import com.fatec.horario.dtos.ClassroomResponse;
import com.fatec.horario.entities.Classroom;

public class ClassroomMapper {

    public static Classroom toEntity(ClassroomRequest request) {
        Classroom classroom = new Classroom();

        classroom.setName(request.name());
        classroom.setLocation(request.location());
        classroom.setPhysicalResources(request.physicalResources());
        classroom.setSoftwareResources(request.softwareResources());
        classroom.setCapacity(request.capacity());
        classroom.setTemplate(request.template());
        classroom.setPractical(request.practical());

        return classroom;
    }

    public static ClassroomResponse toResponse(Classroom classroom) {
        return new ClassroomResponse(
            classroom.getId(),
            classroom.getName(),
            classroom.getLocation(),
            classroom.getPhysicalResources(),
            classroom.getSoftwareResources(),
            classroom.getCapacity(),
            classroom.getTemplate(),
            classroom.getPractical()
        );

    }

}
