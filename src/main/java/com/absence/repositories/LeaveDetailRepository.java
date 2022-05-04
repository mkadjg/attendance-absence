package com.absence.repositories;

import com.absence.models.LeaveDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveDetailRepository extends JpaRepository<LeaveDetail, String> {
}
