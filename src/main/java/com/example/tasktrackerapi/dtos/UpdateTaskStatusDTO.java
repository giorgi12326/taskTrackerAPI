package com.example.tasktrackerapi.dtos;

import com.example.tasktrackerapi.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTaskStatusDTO {

    @NotNull(message = "Status is required")
    private TaskStatus status;
}