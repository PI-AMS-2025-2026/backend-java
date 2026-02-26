package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.AcademicSemester;

@Repository
public interface AcademicSemesterRepository extends JpaRepository<AcademicSemester, Long> {

}
