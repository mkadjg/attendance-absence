package com.absence.repositories;

import com.absence.models.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubmissionStatusRepository extends JpaRepository<SubmissionStatus, String> {

    @Query("select ss from SubmissionStatus ss where ss.submissionStatusName =:submissionStatusName")
    SubmissionStatus findBySubmissionStatusName(String submissionStatusName);
}
