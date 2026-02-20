package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.CourseUser;

@Repository
public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {

    void deleteByUserId(Long id);
}
