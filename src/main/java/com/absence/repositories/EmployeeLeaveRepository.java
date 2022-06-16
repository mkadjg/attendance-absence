package com.absence.repositories;

import com.absence.models.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, String> {

    @Query("select el from EmployeeLeave el where el.employee.employeeId =:employeeId")
    List<EmployeeLeave> findByEmployeeId(String employeeId);

    @Query("select el from EmployeeLeave el where el.employee.employeeId =:employeeId " +
            "and el.leaveType.leaveTypeId =:leaveTypeId ")
    EmployeeLeave findByLeaveTypeAndEmployeeId(String leaveTypeId, String employeeId);

}