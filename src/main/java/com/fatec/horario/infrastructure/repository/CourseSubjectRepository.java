package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.CourseSubject;

@Repository
public interface CourseSubjectRepository extends JpaRepository<CourseSubject, Long> {

    void deleteBySubjectId(Long subjectId);
}
