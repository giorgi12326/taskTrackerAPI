package com.example.tasktrackerapi.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDTO {
    private String username;
    private String password;
}
