package com.absence.repositories;

import com.absence.controllers.JobTitleController;
import com.absence.models.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobTitleRepository extends JpaRepository<JobTitle, String> {

    @Query("select jt from JobTitle jt where jt.division.divisionId=:divisionId")
    List<JobTitle> findAllByDivisionId(String divisionId);
}