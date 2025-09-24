package com.example.tasktrackerapi.controller;

import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
