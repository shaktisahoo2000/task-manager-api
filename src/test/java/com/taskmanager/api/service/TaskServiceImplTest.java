package com.taskmanager.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    // 1. Create Task

    @Test
    void createTask_shouldReturnTaskResponse() {

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Learn Spring Boot", response.getTitle());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // 2. Get All Tasks

    @Test
    void getAllTasks_shouldReturnTaskList() {

        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> response = taskService.getAllTasks();

        assertEquals(1, response.size());
        assertEquals("Learn Spring Boot", response.get(0).getTitle());
    }

    // 3. Get Task By ID

    @Test
    void getTaskById_shouldReturnTaskResponse() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    // 4. Get Task By ID - Not Found

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskNotFound() {

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(99L));
    }

    // 5. Update Task

    @Test
    void updateTask_shouldReturnUpdatedTaskResponse() {

        request.setTitle("Learn Microservices");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTask(1L, request);

        assertEquals("Learn Microservices", response.getTitle());

        verify(taskRepository).save(any(Task.class));
    }

    // 6. Update Task - Not Found

    @Test
    void updateTask_shouldThrowExceptionWhenTaskNotFound() {

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask(99L, request));
    }

    // 7. Delete Task

    @Test
    void deleteTask_shouldDeleteTaskSuccessfully() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }

    // 8. Delete Task - Not Found

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskNotFound() {

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteTask(99L));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    // 9. Search Tasks By Title

    @Test
    void searchTasksByTitle_ShouldReturnMatchingTasks() {

        Task testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Learn Java");
        testTask.setDescription("Practice Java concepts");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.MEDIUM);

        when(taskRepository.findByTitleContainingIgnoreCase("java"))
                .thenReturn(List.of(testTask));

        List<TaskResponse> result = taskService.searchTasksByTitle("java");

        assertEquals(1, result.size());
        assertEquals("Learn Java", result.get(0).getTitle());

        verify(taskRepository).findByTitleContainingIgnoreCase("java");
    }

    // 10. Search Tasks - Empty Result

    @Test
    void searchTasksByTitle_ShouldReturnEmptyList() {

        when(taskRepository.findByTitleContainingIgnoreCase("python"))
                .thenReturn(List.of());

        List<TaskResponse> result = taskService.searchTasksByTitle("python");

        assertTrue(result.isEmpty());
    }

    // 11. Filter Tasks By Status

    @Test
    void filterTasksByStatus_ShouldReturnMatchingTasks() {

        Task testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Learn Java");
        testTask.setDescription("Practice Java concepts");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.MEDIUM);

        when(taskRepository.findByStatus(TaskStatus.TODO))
                .thenReturn(List.of(testTask));

        List<TaskResponse> result = taskService.filterTasksByStatus(TaskStatus.TODO);

        assertEquals(1, result.size());
        assertEquals(TaskStatus.TODO, result.get(0).getStatus());
    }

    // 12. Filter Tasks - Empty Result

    @Test
    void filterTasksByStatus_ShouldReturnEmptyList() {

        when(taskRepository.findByStatus(TaskStatus.COMPLETED))
                .thenReturn(List.of());

        List<TaskResponse> result =
                taskService.filterTasksByStatus(TaskStatus.COMPLETED);

        assertTrue(result.isEmpty());
    }

    // 13. Pagination and Sorting

    @Test
    void getAllTasksWithPagination_ShouldReturnPaginatedTasks() {

        Task testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Learn Java");
        testTask.setDescription("Practice Java concepts");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.MEDIUM);

        Pageable pageable = PageRequest.of(
                0, 5, Sort.by("title").ascending()
        );

        Page<Task> taskPage = new PageImpl<>(
                List.of(testTask),
                pageable,
                1
        );

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("Learn Java", result.getContent().get(0).getTitle());

        verify(taskRepository).findAll(pageable);
    }
}
