package com.example.tasktrackerapi.config;

import com.example.tasktrackerapi.entity.Project;
import com.example.tasktrackerapi.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class Config {
    private final ProjectRepository projectRepository;
    @Bean
    CommandLineRunner run(){
        return args -> {
            Project project = new Project();
            project.setName("Project 1");
            project.setDescription("Project 1");
            projectRepository.save(project);
        };
    }
}
