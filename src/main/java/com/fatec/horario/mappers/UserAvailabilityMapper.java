package com.fatec.horario.mappers;

import com.fatec.horario.dtos.UserAvailabilityRequest;
import com.fatec.horario.dtos.UserAvailabilityResponse;
import com.fatec.horario.entities.UserAvailability;

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