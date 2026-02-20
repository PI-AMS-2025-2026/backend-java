package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.User;
import com.fatec.horario.dto.UserRequest;
import com.fatec.horario.dto.UserResponse;

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
                user.getAccessLevel() != null ? AccessLevelMapper.toResponse(user.getAccessLevel()) : null);
    }
}
