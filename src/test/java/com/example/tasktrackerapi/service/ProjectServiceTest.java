package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.ProjectCreateDTO;
import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.entity.Project;
import com.example.tasktrackerapi.entity.User;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.ProjectMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDTO projectDTO;
    private ProjectCreateDTO projectCreateDTO;
    private User owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = new User();
        owner.setId(1L);

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .owner(owner)
                .tasks(new ArrayList<>())
                .build();

        projectDTO = new ProjectDTO("Test Project", "Test Description", null, null, new ArrayList<>(), null);
        projectCreateDTO = new ProjectCreateDTO("Test Project", "Test Description", 1L);
    }

    @Test
    void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDtos(List.of(project))).thenReturn(List.of(projectDTO));

        List<ProjectDTO> result = projectService.getAllProjects();

        assertEquals(1, result.size());
        verify(projectRepository).findAll();
        verify(projectMapper).toDtos(anyList());
    }

    @Test
    void testGetProjectById_Found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDTO);

        ProjectDTO result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void testCreateProject_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(projectMapper.toEntity(projectCreateDTO)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDTO);

        ProjectDTO result = projectService.createProject(projectCreateDTO);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository).save(project);
    }

    @Test
    void testCreateProject_OwnerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.createProject(projectCreateDTO));
    }

    @Test
    void testUpdateProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        doNothing().when(projectMapper).updateEntity(projectCreateDTO, project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDTO);

        ProjectDTO result = projectService.updateProject(1L, projectCreateDTO);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository).save(project);
    }

    @Test
    void testUpdateProject_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(1L, projectCreateDTO));
    }

    @Test
    void testUpdateProject_OwnerNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(1L, projectCreateDTO));
    }

    @Test
    void testDeleteProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectRepository).delete(project);

        assertDoesNotThrow(() -> projectService.deleteProject(1L));
        verify(projectRepository).delete(project);
    }

    @Test
    void testDeleteProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(1L));
    }
}
