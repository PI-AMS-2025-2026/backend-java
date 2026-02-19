package com.fatec.horario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.entities.TechAxis;

@Repository
public interface TechAxisRepository extends JpaRepository<TechAxis, Long> {

}
