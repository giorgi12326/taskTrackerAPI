package com.example.tasktrackerapi.mapper;

import com.example.tasktrackerapi.dtos.ProjectCreateDTO;
import com.example.tasktrackerapi.dtos.ProjectDTO;
import com.example.tasktrackerapi.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class,UserMapper.class})
public interface ProjectMapper {

    ProjectDTO toDto(Project project);

    List<ProjectDTO> toDtos(List<Project> projects);

    Project toEntity(ProjectDTO projectDTO);

    Project toEntity(ProjectCreateDTO projectDTO);

    void updateEntity(ProjectCreateDTO dto, @MappingTarget Project entity);

}
