package com.absence.repositories;

import com.absence.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    @Query("select at from Attendance at where at.attendanceDate =:attendanceDate and at.employee.employeeId =:employeeId")
    Optional<Attendance> findByAttendanceDateAndEmployeeId(Date attendanceDate, String employeeId);

    @Query("select at from Attendance at where " +
            "at.attendanceDate between :startDate and :endDate " +
            "and at.employee.employeeId =:employeeId " +
            "order by at.attendanceDate")
    List<Attendance> findHistoriesAttendance(Date startDate, Date endDate, String employeeId);

    @Query("select at from Attendance at where " +
            "at.attendanceDate between :startDate and :endDate " +
            "and at.employee.employeeId =:employeeId " +
            "and at.attendanceType.attendanceTypeId = :attendanceTypeId " +
            "order by at.attendanceDate")
    List<Attendance> findHistoriesAttendance(Date startDate, Date endDate, String attendanceTypeId, String employeeId);
}
