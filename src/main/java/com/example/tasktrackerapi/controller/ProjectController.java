package com.example.tasktrackerapi.controller;

import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
public class ProjectController {
    ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO created = projectService.createProject(projectDTO);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectDTO projectDTO
    ) {
        ProjectDTO updated = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
