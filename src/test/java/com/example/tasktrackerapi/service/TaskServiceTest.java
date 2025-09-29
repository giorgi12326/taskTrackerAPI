package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.TaskCreateDTO;
import com.example.tasktrackerapi.dtos.TaskDTO;
import com.example.tasktrackerapi.dtos.UpdateTaskStatusDTO;
import com.example.tasktrackerapi.dtos.UserResponseDTO;
import com.example.tasktrackerapi.entity.*;
import com.example.tasktrackerapi.exeption.AuthorizationFailedException;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.TaskMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.TaskRepository;
import com.example.tasktrackerapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private Task task;
    private TaskDTO taskDTO;
    private TaskCreateDTO taskCreateDTO;
    private User owner;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Make the owner an ADMIN to fix authorization issues
        owner = User.builder().id(1L).email("owner@test.com").role(User.Role.ADMIN).build();
        project = Project.builder().id(1L).owner(owner).build();

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(1))
                .project(project)
                .assignedUser(owner)
                .build();

        taskDTO = TaskDTO.builder()
                .title("Test Task")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        taskCreateDTO = TaskCreateDTO.builder()
                .title("Test Task")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(1))
                .projectId(1L)
                .assignedUser(new UserResponseDTO("owner@test.com"))
                .build();
    }

    @Test
    void testGetTaskById_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.getTaskById(1L);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void testCreateTask_Success() {
        when(taskMapper.toEntity(taskCreateDTO)).thenReturn(task);
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(owner));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.createTask(taskCreateDTO);
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void testUpdateTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(owner));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.updateTask(1L, taskCreateDTO);
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        doNothing().when(taskRepository).delete(task);

        assertDoesNotThrow(() -> taskService.deleteTask(1L));
    }

    @Test
    void testAssignTaskToUser_Success() {
        User newUser = User.builder().id(2L).email("user2@test.com").role(User.Role.USER).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.assignTaskToUser(1L, 2L);
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void testAssignTaskToUser_Unauthorized() {
        User normalUser = User.builder().id(3L).email("user3@test.com").role(User.Role.USER).build();
        when(userService.getCurrentUserEmail()).thenReturn("user3@test.com");
        when(userRepository.findByEmail("user3@test.com")).thenReturn(Optional.of(normalUser));

        assertThrows(AuthorizationFailedException.class, () -> taskService.assignTaskToUser(1L, 2L));
    }

    @Test
    void testUpdateTaskStatus_Success() {
        User admin = User.builder().id(2L).email("admin@test.com").role(User.Role.ADMIN).build();

        UpdateTaskStatusDTO dto = new UpdateTaskStatusDTO();
        dto.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUserEmail()).thenReturn("admin@test.com");
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO result = taskService.updateTaskStatus(1L, dto);
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void testUpdateTaskStatus_Unauthorized() {
        UpdateTaskStatusDTO dto = new UpdateTaskStatusDTO();
        dto.setStatus(TaskStatus.IN_PROGRESS);

        // Task is assigned to owner@test.com
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Current user is the assigned user
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");

        // userRepository mock is optional but can be included
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));

        assertThrows(AuthorizationFailedException.class, () -> taskService.updateTaskStatus(1L, dto));
    }
    @Test
    void testGetTasksByAssignedUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(taskRepository.findByAssignedUser(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(task)));
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        Page<TaskDTO> page = taskService.getTasksByAssignedUser(1L, PageRequest.of(0,10), null, null);
        assertEquals(1, page.getContent().size());
    }

    @Test
    void testGetTasksWithFilters() {
        Page<Task> tasks = new PageImpl<>(List.of(task));
        when(taskRepository.findByStatusAndPriority(TaskStatus.TODO, TaskPriority.MEDIUM, PageRequest.of(0,10))).thenReturn(tasks);
        when(userService.getCurrentUserEmail()).thenReturn("owner@test.com");
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        Page<TaskDTO> result = taskService.getTasks(PageRequest.of(0,10), TaskStatus.TODO, TaskPriority.MEDIUM);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetTaskById_AccessDenied() {
        User otherUser = User.builder().id(2L).email("other@test.com").role(User.Role.USER).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUserEmail()).thenReturn("other@test.com");
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));

        assertThrows(AuthorizationFailedException.class, () -> taskService.getTaskById(1L));
    }
}
