package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.TaskDTO;
import com.example.tasktrackerapi.entity.Task;
import com.example.tasktrackerapi.entity.TaskPriority;
import com.example.tasktrackerapi.entity.TaskStatus;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.TaskMapper;
import com.example.tasktrackerapi.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testGetAllTasks() {
        Task t1 = new Task(1L, "Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());
        Task t2 = new Task(2L, "Task 2", "Description 2", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(10), TaskPriority.MEDIUM, LocalDateTime.now(), LocalDateTime.now());

        TaskDTO dto1 = new TaskDTO("Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());
        TaskDTO dto2 = new TaskDTO("Task 2", "Description 2", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(10), TaskPriority.MEDIUM, LocalDateTime.now(), LocalDateTime.now());

        List<Task> tasks = Arrays.asList(t1, t2);
        List<TaskDTO> dtos = Arrays.asList(dto1, dto2);

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toDtos(tasks)).thenReturn(dtos);

        List<TaskDTO> result = taskService.getAllTasks();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
        verify(taskRepository).findAll();
        verify(taskMapper).toDtos(tasks);
    }

    @Test
    void testGetTaskById_found() {
        Task task = new Task(1L, "Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());
        TaskDTO dto = new TaskDTO("Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(dto);

        TaskDTO result = taskService.getTaskById(1L);

        assertThat(result.getTitle()).isEqualTo("Task 1");
        assertThat(result.getDescription()).isEqualTo("Description 1");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(result.getPriority()).isEqualTo(TaskPriority.HIGH);
        verify(taskRepository).findById(1L);
        verify(taskMapper).toDto(task);
    }

    @Test
    void testGetTaskById_notFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository).findById(1L);
        verifyNoInteractions(taskMapper);
    }

    @Test
    void testCreateTask() {
        TaskDTO inputDto = new TaskDTO("Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, null, null);
        Task taskEntity = new Task(null, "Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, null, null);
        Task savedEntity = new Task(1L, "Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());
        TaskDTO outputDto = new TaskDTO("Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());

        when(taskMapper.toEntity(inputDto)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(savedEntity);
        when(taskMapper.toDto(savedEntity)).thenReturn(outputDto);

        TaskDTO result = taskService.createTask(inputDto);

        assertThat(result.getTitle()).isEqualTo("Task 1");
        assertThat(result.getDescription()).isEqualTo("Description 1");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(result.getPriority()).isEqualTo(TaskPriority.HIGH);
        verify(taskMapper).toEntity(inputDto);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void testUpdateTask_notFound() {
        TaskDTO dto = new TaskDTO("Updated Task", "Updated Description", TaskStatus.DONE, LocalDate.now().plusDays(2), TaskPriority.LOW, null, null);

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(1L, dto));
        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskMapper);
    }

    @Test
    void testDeleteTask_notFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository).findById(1L);
        verifyNoInteractions(taskMapper);
    }

    @Test
    void testDeleteTask_found() {
        Task task = new Task(1L, "Task 1", "Description 1", TaskStatus.TODO, LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void testUpdateTask_found() {
        TaskDTO dto = new TaskDTO("Updated Task", "Updated Description", TaskStatus.DONE,
                LocalDate.now().plusDays(2), TaskPriority.LOW, null, null);
        Task entity = new Task(1L, "Task 1", "Description 1", TaskStatus.TODO,
                LocalDate.now().plusDays(5), TaskPriority.HIGH, LocalDateTime.now(), LocalDateTime.now());
        Task updatedEntity = new Task(1L, "Updated Task", "Updated Description", TaskStatus.DONE,
                LocalDate.now().plusDays(2), TaskPriority.LOW, LocalDateTime.now(), LocalDateTime.now());
        TaskDTO outputDto = new TaskDTO("Updated Task", "Updated Description", TaskStatus.DONE,
                LocalDate.now().plusDays(2), TaskPriority.LOW, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(taskMapper).updateEntity(dto, entity);
        when(taskRepository.save(entity)).thenReturn(updatedEntity);
        when(taskMapper.toDto(updatedEntity)).thenReturn(outputDto);

        TaskDTO result = taskService.updateTask(1L, dto);

        assertThat(result.getTitle()).isEqualTo("Updated Task");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(result.getPriority()).isEqualTo(TaskPriority.LOW);

        verify(taskRepository).findById(1L);
        verify(taskMapper).updateEntity(dto, entity);
        verify(taskRepository).save(entity);
        verify(taskMapper).toDto(updatedEntity);
    }


}
