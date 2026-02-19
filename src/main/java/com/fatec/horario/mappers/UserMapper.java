package com.fatec.horario.mappers;

import com.fatec.horario.entities.User;
import com.fatec.horario.dtos.UserRequest;
import com.fatec.horario.dtos.UserResponse;

public class UserMapper {

    public static User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        return user;
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getAccessLevel() != null ? AccessLevelMapper.toResponse(user.getAccessLevel()) : null
        );
    }
}
