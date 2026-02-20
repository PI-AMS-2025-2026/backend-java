package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.UserAvailability;
import com.fatec.horario.dto.UserAvailability.UserAvailabilityRequest;
import com.fatec.horario.dto.UserAvailability.UserAvailabilityResponse;

public class UserAvailabilityMapper {

    public static UserAvailability toEntity(UserAvailabilityRequest request) {

        UserAvailability UserAvailability = new UserAvailability();

        UserAvailability.setWeekday(request.weekday());
        UserAvailability.setLessonNumber(request.lessonNumber());

        return UserAvailability;

    }

    public static UserAvailabilityResponse toResponse(UserAvailability userAvailability) {

        return new UserAvailabilityResponse(

                userAvailability.getId(),
                userAvailability.getWeekday(),
                userAvailability.getLessonNumber(),
                userAvailability.getUser() != null ? UserMapper.toResponse(userAvailability.getUser()) : null

        );
    }

}
