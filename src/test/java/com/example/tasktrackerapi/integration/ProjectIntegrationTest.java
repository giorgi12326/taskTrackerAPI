package com.example.tasktrackerapi.integration;

import com.example.tasktrackerapi.dtos.ProjectCreateDTO;
import com.example.tasktrackerapi.dtos.UpdateTaskStatusDTO;
import com.example.tasktrackerapi.dtos.UserResponseDTO;
import com.example.tasktrackerapi.entity.Project;
import com.example.tasktrackerapi.entity.User;
import com.example.tasktrackerapi.mapper.UserMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.UserRepository;
import com.example.tasktrackerapi.security.JwtUtil;
import com.example.tasktrackerapi.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jaxb.core.v2.TODO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    ProjectCreateDTO projectCreateDTO;

    User user1;

    Project project1;

    ObjectMapper objectMapper;

    private String jwtToken;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnaW9yZ2kyIiwiaWF0IjoxNzU5MTUyOTkyLCJleHAiOjE3NTkxNTYyMzJ9.Tg1r7la0d8NcAVP40eZlYXEzHWF4aH-af4ewrn-EpOs";


    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil();
        jwtToken = jwtUtil.generateToken("user1");
        objectMapper = new ObjectMapper();

        user1 = User.builder()
                .email("user1")
                .password("12345678")
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(user1);

        project1 = Project.builder()
                .name("testProject")
                .description("testDescription")
                .owner(user1)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        projectRepository.save(project1);

        projectCreateDTO = ProjectCreateDTO.builder()
                .name("testProject123")
                .description("testDescription123")
                .ownerId(user1.getId())
                .build();
    }

    @Test
    void testGetProject() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].name").value("testProject"))
                .andExpect(jsonPath("$[1].description").value("testDescription"))
                .andExpect(jsonPath("$[1].owner.email").value(project1.getOwner().getEmail()));

        assertTrue(projectRepository.findByName("testProject").isPresent());
    }

    @Test
    void testGetAllProject() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].name").value("testProject"))
                .andExpect(jsonPath("$[1].description").value("testDescription"))
                .andExpect(jsonPath("$[1].owner.email").value(project1.getOwner().getEmail()));

        assertTrue(projectRepository.findByName("testProject").isPresent());
    }

    @Test
    void testCreateProject() throws Exception {
        UserResponseDTO dto = userMapper.toDto(user1);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(projectCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("testProject123"))
                .andExpect(jsonPath("$.description").value("testDescription123"))
                .andExpect(jsonPath("$.owner.email").value(dto.getEmail()));

        assertTrue(projectRepository.findByName("testProject").isPresent());
    }

    @Test
    void testUpdateProject() throws Exception {
        UserResponseDTO dto = userMapper.toDto(user1);

        mockMvc.perform(put("/api/projects/" + project1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(projectCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testProject123"))
                .andExpect(jsonPath("$.description").value("testDescription123"))
                .andExpect(jsonPath("$.owner.email").value(dto.getEmail()));

        assertTrue(projectRepository.findByName("testProject123").isPresent());
    }

    @Test
    void testDeleteProject() throws Exception {
        mockMvc.perform(delete("/api/projects/" + project1.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        assertFalse(projectRepository.findByName("testProject123").isPresent());
    }

    @Test
    void testCreateProject_Unauthenticated() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + EXPIRED_TOKEN)
                        .content(objectMapper.writeValueAsString(projectCreateDTO)))
                .andExpect(status().isUnauthorized());
    }


}
