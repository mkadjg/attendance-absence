package com.absence.repositories;

import com.absence.models.LeaveSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveSubmissionRepository extends JpaRepository<LeaveSubmission, String> {

    @Query("select ls from LeaveSubmission ls where ls.employee.employeeId =:employeeId " +
            "and year(ls.startDate) =:year " +
            "order by ls.startDate desc")
    List<LeaveSubmission> findByEmployeeAndYear(String employeeId, int year);

    @Query("select ls from LeaveSubmission ls where ls.employee.employeeId =:employeeId " +
            "and year(ls.startDate) =:year " +
            "and ls.submissionStatus.submissionStatusId =:submissionStatusId " +
            "order by ls.startDate desc")
    List<LeaveSubmission> findByEmployeeAndYearAndSubmissionStatus(String employeeId, int year, String submissionStatusId);

    @Query("select ls from LeaveSubmission ls where ls.employee.jobTitle.division.divisionId =:divisionId " +
            "and year(ls.startDate) =:year " +
            "and ls.submissionStatus.submissionStatusName = 'Waiting Approval Supervisor' " +
            "order by ls.startDate desc")
    List<LeaveSubmission> findByDivisionAndYearForSupervisor(String divisionId, int year);

    @Query("select ls from LeaveSubmission ls where year(ls.startDate) =:year " +
            "and ls.submissionStatus.submissionStatusName = 'Waiting Approval HRD' " +
            "order by ls.startDate desc")
    List<LeaveSubmission> findByYearForHrd(int year);

}
