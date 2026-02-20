package com.fatec.horario.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.Classroom;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
        List<Classroom> findByTemplate(boolean isTemplate);
}
