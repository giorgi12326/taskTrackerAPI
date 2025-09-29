package com.example.tasktrackerapi.config;

import com.example.tasktrackerapi.entity.Project;
import com.example.tasktrackerapi.entity.Task;
import com.example.tasktrackerapi.entity.TaskPriority;
import com.example.tasktrackerapi.entity.TaskStatus;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@AllArgsConstructor
public class Config {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    @Bean
    CommandLineRunner run(){
        return args -> {
            Project project = new Project();
            project.setName("Project 1");
            project.setDescription("Project 1");
            projectRepository.save(project);

            Task task = new Task();
            task.setDescription("Task 1");
            task.setStatus(TaskStatus.DONE);
            task.setPriority(TaskPriority.HIGH);
            task.setTitle("Task 1");
            task.setDueDate(LocalDate.now());
            taskRepository.save(task);

        };
    }
}
