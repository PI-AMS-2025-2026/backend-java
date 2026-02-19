package com.fatec.horario.repositories;

import com.fatec.horario.entities.CourseSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSubjectRepository extends JpaRepository<CourseSubject, Long> {

    void deleteBySubjectId(Long subjectId);
}
