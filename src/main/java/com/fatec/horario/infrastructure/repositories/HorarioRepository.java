package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Horario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByHoraInicio(LocalTime horaInicio);

    List<Horario> findByHoraFim(LocalTime horaFim);

    Page<Horario> findByHoraInicio(LocalTime horaInicio, Pageable pageable);

    Page<Horario> findByHoraFim(LocalTime horaFim, Pageable pageable);

    Page<Horario> findByHoraInicioAndHoraFim(LocalTime horaInicio, LocalTime horaFim, Pageable pageable);
}