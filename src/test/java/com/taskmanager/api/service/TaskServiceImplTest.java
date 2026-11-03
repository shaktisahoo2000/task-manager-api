package com.taskmanager.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taskmanager.api.dto.TaskRequest;
import com.taskmanager.api.dto.TaskResponse;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.TaskPriority;
import com.taskmanager.api.entity.TaskStatus;
import com.taskmanager.api.exception.TaskNotFoundException;
import com.taskmanager.api.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskRequest request;
    private Task task;

    @BeforeEach
    void setUp() {

        request = new TaskRequest();
        request.setTitle("Learn Spring Boot");
        request.setDescription("Complete Task Manager project");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        task = new Task();
        task.setId(1L);
        task.setTitle("Learn Spring Boot");
        task.setDescription("Complete Task Manager project");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
    }

    @Test
    void createTask_shouldReturnTaskResponse() {

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Learn Spring Boot", response.getTitle());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getAllTasks_shouldReturnTaskList() {

        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> response = taskService.getAllTasks();

        assertEquals(1, response.size());
        assertEquals("Learn Spring Boot", response.get(0).getTitle());
    }

    @Test
    void getTaskById_shouldReturnTaskResponse() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskNotFound() {

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(99L));
    }

    @Test
    void updateTask_shouldReturnUpdatedTaskResponse() {

        request.setTitle("Learn Microservices");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTask(1L, request);

        assertEquals("Learn Microservices", response.getTitle());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskNotFound() {

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask(99L, request));
    }

    @Test
    void deleteTask_shouldDeleteTaskSuccessfully() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskNotFound() {

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteTask(99L));

        verify(taskRepository, never()).delete(any(Task.class));
    }
}