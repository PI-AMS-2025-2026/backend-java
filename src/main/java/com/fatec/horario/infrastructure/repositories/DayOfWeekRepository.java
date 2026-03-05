package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Long> {

}