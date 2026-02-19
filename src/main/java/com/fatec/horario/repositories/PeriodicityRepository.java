package com.fatec.horario.repositories;

import com.fatec.horario.entities.Periodicity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodicityRepository extends JpaRepository<Periodicity, Long> {
  
}
