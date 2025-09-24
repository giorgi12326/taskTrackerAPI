package com.example.tasktrackerapi.repository;

import com.example.tasktrackerapi.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
