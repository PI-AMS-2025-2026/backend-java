package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.UserAvailability;

@Repository
public interface UserAvailabilityRepository extends JpaRepository<UserAvailability, Long> {

    void deleteByUserId(Long id);
}
