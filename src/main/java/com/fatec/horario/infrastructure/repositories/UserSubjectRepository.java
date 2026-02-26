package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.UserSubject;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Long> {

    void deleteByUserId(Long userId);

    void deleteBySubjectId(Long subjectId);
}
