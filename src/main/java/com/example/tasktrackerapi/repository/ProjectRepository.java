package com.example.tasktrackerapi.repository;

import com.example.tasktrackerapi.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    public Optional<Project> findByName(String name);
}
