package com.absence.controllers;

import com.absence.dto.ProjectRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Project;
import com.absence.repositories.DivisionRepository;
import com.absence.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    DivisionRepository divisionRepository;

    @GetMapping("/find-all")
    public ResponseEntity<Object> findAll() {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(projectRepository.findAll())
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody ProjectRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) {
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setProjectDesc(dto.getProjectDesc());
        project.setDivision(divisionRepository.findById(dto.getDivisionId()).orElse(null));
        project.setCreatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(projectRepository.save(project))
                .message("Successfully create data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/{projectId}")
    public ResponseEntity<Object> update(@PathVariable("projectId") String projectId,
                                         @RequestBody ProjectRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project != null) {
            project.setProjectName(dto.getProjectName());
            project.setProjectDesc(dto.getProjectDesc());
            project.setDivision(divisionRepository.findById(dto.getDivisionId()).orElse(null));
            project.setUpdatedBy(userAuditId);

            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(projectRepository.save(project))
                    .message("Successfully update data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<Object> delete(@PathVariable("projectId") String projectId) throws ResourceNotFoundException {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project != null) {
            projectRepository.delete(project);
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(null)
                    .message("Successfully delete data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

    @GetMapping("/find-by-division/{divisionId}")
    public ResponseEntity<Object> findByDivision(@PathVariable String divisionId) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(projectRepository.findByDivisionId(divisionId))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

}
