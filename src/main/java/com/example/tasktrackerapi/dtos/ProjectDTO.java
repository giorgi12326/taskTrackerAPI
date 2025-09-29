package com.example.tasktrackerapi.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectDTO {

    @NotBlank(message = "Project name must not be blank")
    @Size(max = 100, message = "Project name must be at most 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

}