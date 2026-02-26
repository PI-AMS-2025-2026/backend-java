package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    void deleteByProfessorId(Long id);

    void deleteBySubjectId(Long id);

}
