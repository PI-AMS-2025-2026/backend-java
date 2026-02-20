package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.Modality;

@Repository
public interface ModalityRepository extends JpaRepository<Modality, Long> {
}
