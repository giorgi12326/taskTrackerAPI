package com.example.tasktrackerapi.controller;

import com.example.tasktrackerapi.dtos.*;
import com.example.tasktrackerapi.entity.TaskPriority;
import com.example.tasktrackerapi.entity.TaskStatus;
import com.example.tasktrackerapi.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDTO taskDTO;
    private TaskCreateDTO taskCreateDTO;
    private TaskAssignmentDTO taskAssignmentDTO;
    private UpdateTaskStatusDTO updateTaskStatusDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        taskDTO = TaskDTO.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(1))
                .projectId(1L)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        taskCreateDTO = TaskCreateDTO.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(1))
                .projectId(1L)
                .build();

        taskAssignmentDTO = new TaskAssignmentDTO();
        taskAssignmentDTO.setTaskId(1L);
        taskAssignmentDTO.setUserId(2L);

        updateTaskStatusDTO = new UpdateTaskStatusDTO(TaskStatus.IN_PROGRESS);
    }

    @Test
    void testGetAllTasks() {
        Page<TaskDTO> page = new PageImpl<>(Collections.singletonList(taskDTO));
        when(taskService.getTasks(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(page);

        ResponseEntity<Page<TaskDTO>> response = taskController.getAllTasks(0, 10, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        verify(taskService, times(1)).getTasks(any(Pageable.class), any(), any());
    }

    @Test
    void testGetTaskById() {
        when(taskService.getTaskById(1L)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.getTaskById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Task", response.getBody().getTitle());
        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testCreateTask() {
        when(taskService.createTask(taskCreateDTO)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.createTask(taskCreateDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Task", response.getBody().getTitle());
        verify(taskService, times(1)).createTask(taskCreateDTO);
    }

    @Test
    void testUpdateTask() {
        when(taskService.updateTask(1L, taskCreateDTO)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, taskCreateDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskService, times(1)).updateTask(1L, taskCreateDTO);
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskService).deleteTask(1L);

        ResponseEntity<Void> response = taskController.deleteTask(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void testAssignTask() {
        when(taskService.assignTaskToUser(1L, 2L)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.assignTask(taskAssignmentDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Task", response.getBody().getTitle());
        verify(taskService, times(1)).assignTaskToUser(1L, 2L);
    }

    @Test
    void testUpdateTaskStatus() {
        when(taskService.updateTaskStatus(1L, updateTaskStatusDTO)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.updateTaskStatus(1L, updateTaskStatusDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskService, times(1)).updateTaskStatus(1L, updateTaskStatusDTO);
    }

    @Test
    void testGetTasksByAssignedUser() {
        Page<TaskDTO> page = new PageImpl<>(Collections.singletonList(taskDTO));
        when(taskService.getTasksByAssignedUser(eq(1L), any(Pageable.class), any(), any()))
                .thenReturn(page);

        ResponseEntity<Page<TaskDTO>> response = taskController.getTasksByAssignedUser(1L, 0, 10, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        verify(taskService, times(1)).getTasksByAssignedUser(eq(1L), any(Pageable.class), any(), any());
    }
}
