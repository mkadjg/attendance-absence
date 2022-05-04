package com.absence.repositories;

import com.absence.models.Division;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepository extends JpaRepository<Division, String> {
}
