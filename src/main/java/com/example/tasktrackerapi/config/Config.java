package com.example.tasktrackerapi.config;

import com.example.tasktrackerapi.entity.*;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.TaskRepository;
import com.example.tasktrackerapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@AllArgsConstructor
public class Config {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Bean
    CommandLineRunner run(){
        return args -> {

            User user = new User();
            user.setRole(User.Role.USER);
            user.setEmail("giorgi");
            user.setPassword(passwordEncoder.encode("giorgi"));
            userRepository.save(user);

            Project project = new Project();
            project.setName("Project 1");
            project.setDescription("Project 1");
            project.setOwner(user);
            projectRepository.save(project);

            Task task = new Task();
            task.setDescription("Task 1");
            task.setStatus(TaskStatus.DONE);
            task.setPriority(TaskPriority.HIGH);
            task.setTitle("Task 1");
            task.setDueDate(LocalDate.now());
            task.setProject(project);
            task.setAssignedUser(user);
            taskRepository.save(task);

        };
    }
}
