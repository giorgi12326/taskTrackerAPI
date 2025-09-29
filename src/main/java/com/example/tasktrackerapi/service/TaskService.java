package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.TaskCreateDTO;
import com.example.tasktrackerapi.dtos.TaskDTO;
import com.example.tasktrackerapi.dtos.UpdateTaskStatusDTO;
import com.example.tasktrackerapi.entity.*;
import com.example.tasktrackerapi.exeption.AuthorizationFailedException;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.TaskMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.TaskRepository;
import com.example.tasktrackerapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    public Page<TaskDTO> getTasks(Pageable pageable, TaskStatus status, TaskPriority priority) {
        Page<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findByStatusAndPriority(status, priority, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(priority, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }
        List<TaskDTO> filtered = tasks.stream()
                .filter(task -> projectOrTaskOwnerAccess(task, userService.getCurrentUserEmail()))
                .map(taskMapper::toDto)
                .toList();

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Task not found with id " + id));
        checkTaskAccessProjectOrTaskOwner(task);

        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDTO createTask(TaskCreateDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        checkTaskAccessProjectOrTaskOwner(task);

        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + taskDTO.getProjectId()));

        User user = userRepository.findByEmail(taskDTO.getAssignedUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + taskDTO.getAssignedUser().getEmail()));

        task.setAssignedUser(user);
        task.setProject(project);

        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskCreateDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Task not found with id " + id));
        checkTaskAccessProjectOrTaskOwner(task);

        if (taskDTO.getProjectId() != null) {
            Project project = projectRepository.findById(taskDTO.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + taskDTO.getProjectId()));
            task.setProject(project);
        }

        if (taskDTO.getAssignedUser() != null && taskDTO.getAssignedUser().getEmail() != null) {
            if (task.getAssignedUser() == null ||
                    !task.getAssignedUser().getEmail().equals(taskDTO.getAssignedUser().getEmail())) {
                User user = userRepository.findByEmail(taskDTO.getAssignedUser().getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + taskDTO.getAssignedUser().getEmail()));
                task.setAssignedUser(user);
            }
        }

        taskMapper.updateEntity(taskDTO, task);
        Task updated = taskRepository.save(task);
        return taskMapper.toDto(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Task not found with id " + id));
        checkTaskAccessProjectOrTaskOwner(task);

        taskRepository.delete(task);
    }

    public TaskDTO assignTaskToUser(Long taskId, Long userId) {
        isAdminOrManager(userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found  with id " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found  with id " + userId));

        task.setAssignedUser(user);
        Task save = taskRepository.save(task);
        return taskMapper.toDto(save);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, UpdateTaskStatusDTO dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + taskId));

        if(task.getAssignedUser() != null && task.getAssignedUser().getEmail().equals(userService.getCurrentUserEmail()))
            throw new AuthorizationFailedException("You are not allowed to update this task");

        task.setStatus(dto.getStatus());
        Task updated = taskRepository.save(task);

        return taskMapper.toDto(updated);
    }

    public Page<TaskDTO> getTasksByAssignedUser(Long userId, Pageable pageable, TaskStatus status, TaskPriority priority) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found: " + userId));

        Page<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findByAssignedUserAndStatusAndPriority(user, status, priority, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByAssignedUserAndStatus(user, status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByAssignedUserAndPriority(user, priority, pageable);
        } else {
            tasks = taskRepository.findByAssignedUser(user, pageable);
        }

        List<TaskDTO> filtered = tasks.stream()
                .filter(task -> projectOrTaskOwnerAccess(task, userService.getCurrentUserEmail()))
                .map(taskMapper::toDto)
                .toList();

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    private void checkTaskAccessProjectOrTaskOwner(Task task) {
        String currentUserEmail = userService.getCurrentUserEmail();
        if (!projectOrTaskOwnerAccess(task, currentUserEmail)) {
            throw new AuthorizationFailedException("Not allowed to access this task");
        }
    }
    private boolean projectOrTaskOwnerAccess(Task task, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserEmail));

        return task.getProject().getOwner().getEmail().equals(currentUserEmail) ||
                (task.getAssignedUser() != null && task.getAssignedUser().getEmail().equals(currentUserEmail) ||
                        user.getRole().equals(User.Role.ADMIN));
    }

    private void isAdminOrManager(Long userId) {
        String currentUser = userService.getCurrentUserEmail();
        User user = userRepository.findByEmail(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("User not found  with id " + userId));
        if (!(user.getRole() == User.Role.MANAGER || user.getRole() == User.Role.ADMIN)) {
            throw new AuthorizationFailedException("Only MANAGER or ADMIN can assign tasks");
        }
    }

}
