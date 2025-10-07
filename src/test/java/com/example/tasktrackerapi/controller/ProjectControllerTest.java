package com.example.tasktrackerapi.controller;

import com.example.tasktrackerapi.dtos.ProjectCreateDTO;
import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.exeption.GlobalExceptionHandler;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.security.JwtUtil;
import com.example.tasktrackerapi.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;
    private ProjectDTO projectDTO;
    private ProjectCreateDTO projectCreateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler()) // <- register handler
                .build();
        objectMapper = new ObjectMapper();

        projectDTO = new ProjectDTO("Test Project", "Description", null, null, new ArrayList<>(), null);
        projectCreateDTO = new ProjectCreateDTO("Test Project", "Description", 1L);
    }

    @Test
    void testGetAllProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(projectDTO));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetProjectById_Found() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(projectDTO);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testGetProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(2L)).thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(get("/api/projects/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Project not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testCreateProject() throws Exception {
        when(projectService.createProject(projectCreateDTO)).thenReturn(projectDTO);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testUpdateProject_Success() throws Exception {
        when(projectService.updateProject(1L, projectCreateDTO)).thenReturn(projectDTO);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void testUpdateProject_NotFound() throws Exception {
        when(projectService.updateProject(1L, projectCreateDTO)).thenThrow(new ResourceNotFoundException("Project not found"));
        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProject_Success() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProject_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Project not found")).when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNotFound());
    }
}
