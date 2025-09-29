package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.entity.Project;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
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

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return projectMapper.toDto(project);
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = projectMapper.toEntity(projectDTO);
        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }

    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        projectMapper.updateEntity(projectDTO, project);

        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        projectRepository.delete(project);
    }
}
