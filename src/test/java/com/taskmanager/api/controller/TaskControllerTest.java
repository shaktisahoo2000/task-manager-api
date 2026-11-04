package com.taskmanager.api.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.taskmanager.api.dto.TaskRequest;
import com.taskmanager.api.dto.TaskResponse;
import com.taskmanager.api.entity.TaskPriority;
import com.taskmanager.api.entity.TaskStatus;
import com.taskmanager.api.service.TaskService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    // Test 1: Create Task
    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {

        TaskRequest request = new TaskRequest();
        request.setTitle("Learn Spring Boot");
        request.setDescription("Complete REST API project");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Learn Spring Boot");
        response.setDescription("Complete REST API project");
        response.setStatus(TaskStatus.TODO);
        response.setPriority(TaskPriority.HIGH);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(taskService.createTask(org.mockito.ArgumentMatchers.any(TaskRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/tasks/createTask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    // Test 2: Get All Tasks
    @Test
    void getAllTasks_ShouldReturnTaskList() throws Exception {

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Learn Java");
        response.setDescription("Practice Java concepts");
        response.setStatus(TaskStatus.INPROGRESS);
        response.setPriority(TaskPriority.MEDIUM);

        when(taskService.getAllTasks()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/tasks/getAllTasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Learn Java"))
                .andExpect(jsonPath("$[0].status").value("INPROGRESS"));
    }

    // Test 3: Get Task By ID
    @Test
    void getTaskById_ShouldReturnTask() throws Exception {

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Learn Microservices");
        response.setStatus(TaskStatus.TODO);
        response.setPriority(TaskPriority.HIGH);

        when(taskService.getTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/getTask/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Learn Microservices"));
    }

    // Test 4: Update Task
    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {

        TaskRequest request = new TaskRequest();
        request.setTitle("Learn Advanced Java");
        request.setDescription("Practice advanced concepts");
        request.setStatus(TaskStatus.COMPLETED);
        request.setPriority(TaskPriority.HIGH);

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Learn Advanced Java");
        response.setDescription("Practice advanced concepts");
        response.setStatus(TaskStatus.COMPLETED);
        response.setPriority(TaskPriority.HIGH);

        when(taskService.updateTask(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(TaskRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/tasks/updateTask/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Learn Advanced Java"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    // Test 5: Delete Task
    @Test
    void deleteTask_ShouldReturnSuccessMessage() throws Exception {

        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/deleteTask/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted successfully"));
    }

    // Test 6: Create Task With Invalid Title
    @Test
    void createTask_WithBlankTitle_ShouldReturnBadRequest() throws Exception {

        TaskRequest request = new TaskRequest();
        request.setTitle("");
        request.setDescription("Invalid task");

        mockMvc.perform(post("/api/tasks/createTask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void searchTasksByTitle_ShouldReturnMatchingTasks() throws Exception {

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Learn Java");
        response.setStatus(TaskStatus.TODO);

        when(taskService.searchTasksByTitle("java"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/tasks/search")
                .param("title", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Learn Java"));
    }
    
    @Test
    void filterTasksByStatus_ShouldReturnMatchingTasks() throws Exception {

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Learn Java");
        response.setStatus(TaskStatus.TODO);

        when(taskService.filterTasksByStatus(TaskStatus.TODO))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/tasks/filter")
                .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }
    
    @Test
    void getTasksWithPagination_ShouldReturnPaginatedTasks() throws Exception {

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Learn Java");
        response.setStatus(TaskStatus.TODO);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());

        Page<TaskResponse> page = new PageImpl<>(
                List.of(response),
                pageable,
                1
        );

        when(taskService.getAllTasks(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/tasks/page")
                .param("page", "0")
                .param("size", "5")
                .param("sortBy", "title")
                .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Learn Java"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}