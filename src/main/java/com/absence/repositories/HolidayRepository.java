package com.absence.repositories;

import com.absence.models.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, String> {
}
