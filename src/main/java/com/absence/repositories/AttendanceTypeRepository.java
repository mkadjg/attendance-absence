package com.absence.repositories;

import com.absence.models.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceTypeRepository extends JpaRepository<AttendanceType, String> {

    @Query("select aty from AttendanceType aty where aty.attendanceTypeName =:attendanceTypeName")
    AttendanceType findByAttendanceTypeName(String attendanceTypeName);

}
