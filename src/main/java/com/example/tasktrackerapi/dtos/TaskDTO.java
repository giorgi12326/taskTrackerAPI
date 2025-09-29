package com.example.tasktrackerapi.dtos;

import com.example.tasktrackerapi.entity.TaskPriority;
import com.example.tasktrackerapi.entity.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.tasktrackerapi.entity.TaskPriority;
import com.example.tasktrackerapi.entity.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}