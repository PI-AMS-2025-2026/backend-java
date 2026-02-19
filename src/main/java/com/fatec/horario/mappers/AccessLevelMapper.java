package com.fatec.horario.mappers;

import org.springframework.stereotype.Component;

import com.fatec.horario.dtos.AccessLevelRequest;
import com.fatec.horario.dtos.AccessLevelResponse;
import com.fatec.horario.entities.AccessLevel;

@Component
public class AccessLevelMapper {

    public static AccessLevel toEntity(AccessLevelRequest request) {
        AccessLevel AccessLevel = new AccessLevel();

        AccessLevel.setLevel(request.level());
        AccessLevel.setDescription(request.description());

        return AccessLevel;
    }

    public static AccessLevelResponse toResponse(AccessLevel AccessLevel) {
        return new AccessLevelResponse(

            AccessLevel.getId(),
            AccessLevel.getLevel(),
            AccessLevel.getDescription()

        );
    }

}
