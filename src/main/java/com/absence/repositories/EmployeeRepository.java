package com.absence.repositories;

import com.absence.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("select e from Employee e where e.employeeId <> :employeeId and e.jobTitle.division.divisionId =:divisionId")
    List<Employee> findAllPartner(String employeeId, String divisionId);

    @Query("select e from Employee e where e.userId =:userId")
    Optional<Employee> findByUserId(String userId);

}
