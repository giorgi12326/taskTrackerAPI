package com.example.tasktrackerapi.dtos;

import com.example.tasktrackerapi.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String username;
    private String password;
    private User.Role role;
}
