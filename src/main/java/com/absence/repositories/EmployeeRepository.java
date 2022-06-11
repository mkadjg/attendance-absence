package com.absence.repositories;

import com.absence.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("select e from Employee e where e.employeeId <> :employeeId and e.division.divisionId =:divisionId")
    List<Employee> findAllPartner(String employeeId, String divisionId);

}
