package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.entity.Project;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.ProjectMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void testGetAllProjects() {
        Project p1 = new Project(1L, "Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        Project p2 = new Project(2L, "Project 2", "Description 2", LocalDateTime.now(), LocalDateTime.now());

        ProjectDTO dto1 = new ProjectDTO("Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        ProjectDTO dto2 = new ProjectDTO("Project 2", "Description 2", LocalDateTime.now(), LocalDateTime.now());

        List<Project> projects = Arrays.asList(p1, p2);
        List<ProjectDTO> dtos = Arrays.asList(dto1, dto2);

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDtos(projects)).thenReturn(dtos);

        List<ProjectDTO> result = projectService.getAllProjects();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
        verify(projectRepository).findAll();
        verify(projectMapper).toDtos(projects);
    }

    @Test
    void testGetProjectById_found() {
        Project project = new Project(1L, "Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        ProjectDTO dto = new ProjectDTO("Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(dto);

        ProjectDTO result = projectService.getProjectById(1L);

        assertThat(result.getName()).isEqualTo("Project 1");
        assertThat(result.getDescription()).isEqualTo("Description 1");
        verify(projectRepository).findById(1L);
        verify(projectMapper).toDto(project);
    }

    @Test
    void testGetProjectById_notFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(1L));
        verify(projectRepository).findById(1L);
        verifyNoInteractions(projectMapper);
    }

    @Test
    void testCreateProject() {
        ProjectDTO inputDto = new ProjectDTO("Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        Project projectEntity = new Project(null, "Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        Project savedEntity = new Project(1L, "Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        ProjectDTO outputDto = new ProjectDTO("Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());

        when(projectMapper.toEntity(inputDto)).thenReturn(projectEntity);
        when(projectRepository.save(projectEntity)).thenReturn(savedEntity);
        when(projectMapper.toDto(savedEntity)).thenReturn(outputDto);

        ProjectDTO result = projectService.createProject(inputDto);

        assertThat(result.getName()).isEqualTo("Project 1");
        assertThat(result.getDescription()).isEqualTo("Description 1");
        verify(projectMapper).toEntity(inputDto);
        verify(projectRepository).save(projectEntity);
        verify(projectMapper).toDto(savedEntity);
    }

    @Test
    void testUpdateProject_found() {
        ProjectDTO dto = new ProjectDTO("Updated Project", "Updated Description", LocalDateTime.now(), LocalDateTime.now());
        Project entity = new Project(1L, "Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        Project savedEntity = new Project(1L, "Updated Project", "Updated Description", LocalDateTime.now(), LocalDateTime.now());
        ProjectDTO outputDto = new ProjectDTO("Updated Project", "Updated Description", LocalDateTime.now(), LocalDateTime.now());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(projectMapper).updateEntity(dto, entity);
        when(projectRepository.save(entity)).thenReturn(savedEntity);
        when(projectMapper.toDto(savedEntity)).thenReturn(outputDto);

        ProjectDTO result = projectService.updateProject(1L, dto);

        assertThat(result.getName()).isEqualTo("Updated Project");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        verify(projectRepository).findById(1L);
        verify(projectMapper).updateEntity(dto, entity);
        verify(projectRepository).save(entity);
        verify(projectMapper).toDto(savedEntity);
    }

    @Test
    void testUpdateProject_notFound() {
        ProjectDTO dto = new ProjectDTO("Updated Project", "Updated Description", LocalDateTime.now(), LocalDateTime.now());
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(1L, dto));
        verify(projectRepository).findById(1L);
        verifyNoMoreInteractions(projectMapper);
    }

    @Test
    void testDeleteProject_found() {
        Project entity = new Project(1L, "Project 1", "Description 1", LocalDateTime.now(), LocalDateTime.now());
        when(projectRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(projectRepository).delete(entity);

        projectService.deleteProject(1L);

        verify(projectRepository).findById(1L);
        verify(projectRepository).delete(entity);
    }

    @Test
    void testDeleteProject_notFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(1L));
        verify(projectRepository).findById(1L);
        verifyNoMoreInteractions(projectRepository);
        verifyNoInteractions(projectMapper);
    }
}
