package com.example.tasktrackerapi.controller;

import com.example.tasktrackerapi.dtos.TaskDTO;
import com.example.tasktrackerapi.entity.TaskPriority;
import com.example.tasktrackerapi.entity.TaskStatus;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTasks() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(
                new TaskDTO("Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now()),
                new TaskDTO("Task 2", "Description 2", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(10), TaskPriority.MEDIUM, LocalDateTime.now(), LocalDateTime.now())
        );

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testGetTaskById() throws Exception {
        TaskDTO task = new TaskDTO("Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());

        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskDTO input = new TaskDTO("New Task", "New Description", TaskStatus.TODO, LocalDate.now().plusDays(3), TaskPriority.LOW, null, null);
        TaskDTO saved = new TaskDTO("New Task", "New Description", TaskStatus.TODO, LocalDate.now().plusDays(3), TaskPriority.LOW, LocalDateTime.now(), LocalDateTime.now());

        when(taskService.createTask(any(TaskDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("LOW"));

        verify(taskService, times(1)).createTask(any(TaskDTO.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskDTO input = new TaskDTO("Updated Task", "Updated Description", TaskStatus.DONE, LocalDate.now().plusDays(2), TaskPriority.HIGH, null, null);
        TaskDTO updated = new TaskDTO("Updated Task", "Updated Description", TaskStatus.DONE, LocalDate.now().plusDays(2), TaskPriority.HIGH, null, LocalDateTime.now());

        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskDTO.class));
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void testGetTaskById_notFound() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound()); // assuming you have @ControllerAdvice handling ResourceNotFoundException
        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testUpdateTask_notFound() throws Exception {
        TaskDTO input = new TaskDTO("Updated Task", "Updated Description", TaskStatus.DONE,
                LocalDate.now().plusDays(2), TaskPriority.HIGH, null, null);

        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).updateTask(eq(1L), any(TaskDTO.class));
    }

    @Test
    void testDeleteTask_notFound() throws Exception {
        doThrow(new ResourceNotFoundException()).when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).deleteTask(1L);
    }

}
