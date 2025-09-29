package com.example.tasktrackerapi.controller;

import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllProjects() throws Exception {
        List<ProjectDTO> projects = Arrays.asList(
                new ProjectDTO("Project 1", "Project 1 Description", LocalDateTime.now(), LocalDateTime.now()),
                new ProjectDTO("Project 2", "Project 2 Description", LocalDateTime.now(), LocalDateTime.now())
        );

        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Project 1"))
                .andExpect(jsonPath("$[0].description").value("Project 1 Description"))
                .andExpect(jsonPath("$[1].name").value("Project 2"))
                .andExpect(jsonPath("$[1].description").value("Project 2 Description"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetProjectById_found() throws Exception {
        ProjectDTO project = new ProjectDTO("Project 1", "Project 1 Description", LocalDateTime.now(), LocalDateTime.now());

        when(projectService.getProjectById(1L)).thenReturn(project);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project 1"))
                .andExpect(jsonPath("$.description").value("Project 1 Description"));

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void testGetProjectById_notFound() throws Exception {
        when(projectService.getProjectById(1L)).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void testCreateProject() throws Exception {
        ProjectDTO input = new ProjectDTO("Project 1", "Project 1 Description", LocalDateTime.now(), LocalDateTime.now());
        ProjectDTO saved = new ProjectDTO("Project 1", "Project 1 Description", LocalDateTime.now(), LocalDateTime.now());

        when(projectService.createProject(any(ProjectDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Project 1"))
                .andExpect(jsonPath("$.description").value("Project 1 Description"));

        verify(projectService, times(1)).createProject(any(ProjectDTO.class));
    }

    @Test
    void testUpdateProject_found() throws Exception {
        ProjectDTO input = new ProjectDTO("Project 1", "Project 1 Description", LocalDateTime.now(), LocalDateTime.now());
        ProjectDTO updated = new ProjectDTO("Updated Project", "Updated Description", LocalDateTime.now(), LocalDateTime.now());

        when(projectService.updateProject(eq(1L), any(ProjectDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectDTO.class));
    }

    @Test
    void testUpdateProject_notFound() throws Exception {
        ProjectDTO input = new ProjectDTO("Project 1", "Project 1 Description", LocalDateTime.now(), LocalDateTime.now());

        when(projectService.updateProject(eq(1L), any(ProjectDTO.class)))
                .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectDTO.class));
    }

    @Test
    void testDeleteProject_found() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProject(1L);
    }

    @Test
    void testDeleteProject_notFound() throws Exception {
        doThrow(new ResourceNotFoundException()).when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).deleteProject(1L);
    }
}
