package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.mapper.ProjectMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public List<ProjectDTO> getAllProjects() {
        return projectMapper.toDtos(projectRepository.findAll());
    }
}
