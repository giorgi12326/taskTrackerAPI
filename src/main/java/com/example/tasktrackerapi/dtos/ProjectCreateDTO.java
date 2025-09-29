package com.example.tasktrackerapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateDTO {

    @NotBlank(message = "Project name must not be blank")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private Long ownerId;
}
