package com.example.tasktrackerapi.integration;

import com.example.tasktrackerapi.dtos.*;
import com.example.tasktrackerapi.entity.*;
import com.example.tasktrackerapi.exeption.ResourceNotFoundException;
import com.example.tasktrackerapi.mapper.TaskMapper;
import com.example.tasktrackerapi.mapper.UserMapper;
import com.example.tasktrackerapi.repository.ProjectRepository;
import com.example.tasktrackerapi.repository.TaskRepository;
import com.example.tasktrackerapi.repository.UserRepository;
import com.example.tasktrackerapi.security.JwtUtil;
import com.example.tasktrackerapi.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.core.type.TypeReference;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class TaskIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    private String jwtToken;

    @Autowired
    ObjectMapper objectMapper;

    User user1;

    @Autowired
    UserRepository userRepository;

    Task task1;

    Project project1;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    private UserMapper userMapper;

    TaskCreateDTO taskCreateDTO;

    TaskDTO taskDTO;

    @Autowired
    private TaskMapper taskMapper;
    private TaskAssignmentDTO taskAssignmentDTO;
    private User user2;
    JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtToken = jwtUtil.generateToken("user1");

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

        task1 = Task.builder()
                .title("testTask")
                .description("testDescription")
                .assignedUser(user1)
                .dueDate(LocalDate.now().plusDays(1))
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .project(project1)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        taskRepository.save(task1);

        taskCreateDTO = TaskCreateDTO.builder()
                .title("testTask")
                .description("testDescription")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(1))
                .projectId(project1.getId())
                .assignedUser(userMapper.toDto(user1))
                .build();

        taskDTO = TaskDTO.builder()
                .title("testTask")
                .description("testDescription")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(1))
                .projectId(project1.getId())
                .assignedUser(userMapper.toDto(user1))
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        user2 = User.builder()
                .email("user123")
                .password("12345678")
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(user2);
        
        taskAssignmentDTO = TaskAssignmentDTO.builder()
                .taskId(task1.getId())
                .userId(user2.getId())
                .build();
   
    }

    @Test
    void testGetAllProject() throws Exception {
        String response = mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        List<TaskDTO> tasks = objectMapper.readValue(
                node.get("content").toString(),
                new TypeReference<List<TaskDTO>>() {}
        );

        TaskDTO actual = tasks.get(1);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createDate","updateDate", "projectId")
                .isEqualTo(taskDTO);

        assertEquals(2, tasks.size());
    }

    @Test
    void testGetTaskById() throws Exception {
        String response = mockMvc.perform(get("/api/tasks/" + task1.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TaskDTO taskDTO1 = objectMapper.readValue(response, TaskDTO.class);

        assertThat(taskDTO1).usingRecursiveComparison()
                .ignoringFields("id", "createDate","updateDate", "projectId")
                .isEqualTo(taskDTO);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskCreateDTO taskCreateDTO = TaskCreateDTO.builder()
                .title("testTaskTemp")
                .description("testDescription")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(1))
                .projectId(project1.getId())
                .assignedUser(userMapper.toDto(user1))
                .build();
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(taskCreateDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        TaskDTO taskDTO1 = objectMapper.readValue(response, TaskDTO.class);

        assertThat(taskDTO1).usingRecursiveComparison()
                .ignoringFields("id", "createDate","updateDate", "projectId")
                .isEqualTo(taskCreateDTO);

        assertTrue(taskRepository.findByTitle("testTaskTemp").isPresent());
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskCreateDTO taskCreateDTO = TaskCreateDTO.builder()
                .title("testTaskTemp")
                .description("testDescription")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(1))
                .projectId(project1.getId())
                .assignedUser(userMapper.toDto(user1))
                .build();
        String response = mockMvc.perform(put("/api/tasks/" + task1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(taskCreateDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        TaskDTO taskDTO1 = objectMapper.readValue(response, TaskDTO.class);

        assertThat(taskDTO1).usingRecursiveComparison()
                .ignoringFields("id", "createDate","updateDate", "projectId")
                .isEqualTo(taskCreateDTO);

        assertTrue(taskRepository.findByTitle("testTaskTemp").isPresent());
        assertFalse(taskRepository.findByTitle("testTask").isPresent());
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/" +  task1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        assertFalse(taskRepository.findByTitle("testTask").isPresent());
    }
    
    @Test
    void testAssignTask() throws Exception{
        mockMvc.perform(post("/api/tasks/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(taskAssignmentDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Task task = taskRepository.findByTitle("testTask").orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        System.out.println(task.getId());
        System.out.println(task.getAssignedUser());
        assertEquals(task.getAssignedUser(),user2);
        assertNotEquals(task.getAssignedUser(),user1);

    }
    @Test
    void testUpdateTaskStatus() throws Exception {
        mockMvc.perform(put("/api/tasks/" + task1.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken("user123"))
                        .content(objectMapper.writeValueAsString(new UpdateTaskStatusDTO(TaskStatus.IN_PROGRESS))))
                .andExpect(status().isOk());

        Task task = taskRepository.findByTitle("testTask").orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        assertEquals(TaskStatus.IN_PROGRESS,task.getStatus());
    }

    @Test
    void testUpdateTaskStatus_DoesntOwnTask() throws Exception {
        mockMvc.perform(put("/api/tasks/" + task1.getId() +"/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken("user123"))
                        .content(objectMapper.writeValueAsString(new UpdateTaskStatusDTO(TaskStatus.IN_PROGRESS))))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTasksByAssignedUser() throws Exception {
        String response = mockMvc.perform(get("/api/tasks/user/" + user1.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        List<TaskDTO> tasks = objectMapper.readValue(
                node.get("content").toString(),
                new TypeReference<List<TaskDTO>>() {}
        );
        assertThat(tasks.get(0)).usingRecursiveComparison()
                .ignoringFields("id", "createDate","updateDate", "projectId")
                .isEqualTo(taskDTO);

    }
}
