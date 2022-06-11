package com.absence.repositories;

import com.absence.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, String> {

    @Query("select p from Project p where p.division.divisionId =:divisionId")
    List<Project> findByDivisionId(String divisionId);

}