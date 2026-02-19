package com.fatec.horario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.entities.AcademicSemester;

@Repository
public interface AcademicSemesterRepository extends JpaRepository<AcademicSemester,Long>{
    
}
