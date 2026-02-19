package com.fatec.horario.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.entities.Modality;

@Repository
public interface ModalityRepository extends JpaRepository<Modality, Long> {
}