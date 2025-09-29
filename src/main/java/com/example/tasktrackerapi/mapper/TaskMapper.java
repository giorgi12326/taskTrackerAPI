package com.example.tasktrackerapi.mapper;

import com.example.tasktrackerapi.dtos.TaskCreateDTO;
import com.example.tasktrackerapi.dtos.TaskDTO;
import com.example.tasktrackerapi.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class,UserMapper.class})
public interface TaskMapper {

    TaskDTO toDto(Task task);

    List<TaskDTO> toDtos(List<Task> tasks);

    Task toEntity(TaskDTO projectDTO);

    Task toEntity(TaskCreateDTO projectDTO);

    void updateEntity(TaskDTO dto, @MappingTarget Task entity);

    void updateEntity(TaskCreateDTO dto, @MappingTarget Task entity);

}
