package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.ProjectCreateDTO;
import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.entity.Project;
import com.example.tasktrackerapi.entity.User;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.ProjectMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    public List<ProjectDTO> getAllProjects() {
        return projectMapper.toDtos(projectRepository.findAll());
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDTO createProject(ProjectCreateDTO projectDTO) {
        User owner = userRepository.findById(projectDTO.getOwnerId())
                .orElseThrow(()-> new ResourceNotFoundException("Owner not found"));

        Project project = projectMapper.toEntity(projectDTO);

        project.setOwner(owner);
        project.setTasks(new ArrayList<>());

        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }


    public ProjectDTO updateProject(Long id, ProjectCreateDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(()->  new ResourceNotFoundException("Project not found"));
        User owner = userRepository.findById(projectDTO.getOwnerId())
                .orElseThrow(()->  new ResourceNotFoundException("Owner not found"));

        projectMapper.updateEntity(projectDTO, project);

        project.setOwner(owner);

        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        projectRepository.delete(project);
    }
}
