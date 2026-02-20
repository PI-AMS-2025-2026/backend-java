package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.ShiftSchedule;

@Repository
public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {
}
