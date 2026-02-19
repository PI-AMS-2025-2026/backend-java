package com.fatec.horario.repositories;

import com.fatec.horario.entities.ShiftScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftScheduleDetailRepository extends JpaRepository<ShiftScheduleDetail, Long> {
}
