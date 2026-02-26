package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.CourseUser;

@Repository
public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {

    void deleteByUserId(Long id);
}
