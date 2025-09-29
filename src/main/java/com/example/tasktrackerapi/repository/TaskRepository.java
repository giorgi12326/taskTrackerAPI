package com.example.tasktrackerapi.repository;

import com.example.tasktrackerapi.entity.Task;
import com.example.tasktrackerapi.entity.TaskPriority;
import com.example.tasktrackerapi.entity.TaskStatus;
import com.example.tasktrackerapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);

    Page<Task> findByAssignedUser(User user, Pageable pageable);

    Page<Task> findByAssignedUserAndStatus(User user, TaskStatus status, Pageable pageable);

    Page<Task> findByAssignedUserAndPriority(User user, TaskPriority priority, Pageable pageable);

    Page<Task> findByAssignedUserAndStatusAndPriority(User user, TaskStatus status, TaskPriority priority, Pageable pageable);

}
