package com.example.tasktrackerapi.controller;

import com.example.tasktrackerapi.dtos.AuthResponseDTO;
import com.example.tasktrackerapi.dtos.UserDTO;
import com.example.tasktrackerapi.dtos.UserLoginDTO;
import com.example.tasktrackerapi.entity.User;
import com.example.tasktrackerapi.exeption.AuthenticationFailedException;
import com.example.tasktrackerapi.mapper.UserMapper;
import com.example.tasktrackerapi.security.JwtUtil;
import com.example.tasktrackerapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody UserLoginDTO userDto) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
            User user = (User) userService.loadUserByUsername(userDto.getUsername());
            return new AuthResponseDTO(jwtUtil.generateToken(user.getUsername()),"Bearer");
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public User register(@RequestBody UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User user1 = new User();
        user1.setEmail(userDTO.getUsername());
        user1.setPassword(userDTO.getPassword());
        user1.setRole(userDTO.getRole());
        userMapper.toEntity(userDTO);

        return userService.saveUser(user1);
    }
}
