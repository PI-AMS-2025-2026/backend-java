package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
