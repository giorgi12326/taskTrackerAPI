package com.example.tasktrackerapi.mapper;

import com.example.tasktrackerapi.dtos.UserDTO;
import com.example.tasktrackerapi.dtos.UserResponseDTO;
import com.example.tasktrackerapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring",uses = {TaskMapper.class,UserMapper.class})

public interface UserMapper {
    UserResponseDTO toDto(User user);

    List<UserResponseDTO> toDtos(List<User> users);

    @Mapping(source = "username", target = "email")
    User toEntity(UserDTO userDto);

}
