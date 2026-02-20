package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.AcademicSemester;

@Repository
public interface AcademicSemesterRepository extends JpaRepository<AcademicSemester, Long> {

}
