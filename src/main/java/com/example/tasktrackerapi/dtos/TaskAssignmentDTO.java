package com.example.tasktrackerapi.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskAssignmentDTO {
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
