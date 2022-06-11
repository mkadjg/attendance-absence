package com.absence.repositories;

import com.absence.models.Sick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface SickRepository extends JpaRepository<Sick, String> {

    @Query("select s from Sick s where " +
            "s.startDate between :startDate and :endDate " +
            "and s.employee.employeeId =:employeeId " +
            "order by s.startDate")
    List<Sick> findSickHistories(Date startDate, Date endDate, String employeeId);

}
