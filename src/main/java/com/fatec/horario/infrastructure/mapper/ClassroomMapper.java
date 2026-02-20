package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.Classroom;
import com.fatec.horario.dto.ClassroomRequest;
import com.fatec.horario.dto.ClassroomResponse;

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
                classroom.getPractical());

    }

}
