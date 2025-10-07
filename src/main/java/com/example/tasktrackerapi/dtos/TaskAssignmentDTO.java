package com.example.tasktrackerapi.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentDTO {
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
