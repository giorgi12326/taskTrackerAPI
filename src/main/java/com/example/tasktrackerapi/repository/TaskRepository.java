package com.example.tasktrackerapi.repository;

import com.example.tasktrackerapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
