package com.example.tasktrackerapi.service;

import com.example.tasktrackerapi.dtos.TaskDTO;
import com.example.tasktrackerapi.entity.Task;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.TaskMapper;
import com.example.tasktrackerapi.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public List<TaskDTO> getAllTasks() {
        List<Task> all = taskRepository.findAll();
        System.out.println(all.size());
        return taskMapper.toDtos(all);
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        taskMapper.updateEntity(taskDTO, task);

        Task updated = taskRepository.save(task);
        return taskMapper.toDto(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        taskRepository.delete(task);
    }
}
