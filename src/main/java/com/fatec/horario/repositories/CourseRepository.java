package com.fatec.horario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.entities.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
}
