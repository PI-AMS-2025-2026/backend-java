package com.fatec.horario.repositories;

import com.fatec.horario.entities.CourseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {

    void deleteByUserId(Long id);
}
