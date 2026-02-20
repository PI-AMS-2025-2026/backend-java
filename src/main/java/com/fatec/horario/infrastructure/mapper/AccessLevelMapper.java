package com.fatec.horario.infrastructure.mapper;

import org.springframework.stereotype.Component;

import com.fatec.horario.domain.model.AccessLevel;
import com.fatec.horario.dto.AcessLevel.AccessLevelRequest;
import com.fatec.horario.dto.AcessLevel.AccessLevelResponse;

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
