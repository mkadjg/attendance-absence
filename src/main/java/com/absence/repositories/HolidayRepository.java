package com.absence.repositories;

import com.absence.models.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, String> {

    @Query("select h from Holiday h where h.holidayDate =:date")
    Optional<Holiday> findByDate(Date date);
}
