package com.fatec.horario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.entities.UserAvailability;

@Repository
public interface UserAvailabilityRepository extends JpaRepository<UserAvailability, Long> {

    void deleteByUserId(Long id);
}
