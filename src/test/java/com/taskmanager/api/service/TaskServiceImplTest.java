package com.taskmanager.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // Helper method to create a sample task
    private Task createSampleTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Learn Spring Boot");
        task.setDescription("Practice unit testing");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        return task;
    }

    @Test
    void createTask_ShouldSaveTask() {
        Task task = createSampleTask();

        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.createTask(task);

        assertEquals("Learn Spring Boot", result.getTitle());
        assertEquals(1L, result.getId());
        verify(taskRepository).save(task);
    }

    @Test
    void createTask_ShouldThrowException_WhenTitleIsNull() {
        Task task = createSampleTask();
        task.setTitle(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(task));

        assertEquals("Task title is required", exception.getMessage());
    }

    @Test
    void getAllTasks_ShouldReturnTaskList() {
        List<Task> tasks = List.of(createSampleTask());

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Learn Spring Boot", result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenTaskExists() {
        Task task = createSampleTask();

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Learn Spring Boot", result.getTitle());
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(999L));

        assertEquals("Task not found with id: 999", exception.getMessage());
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        Task existingTask = createSampleTask();

        Task updatedTask = new Task();
        updatedTask.setTitle("Learn Advanced Spring Boot");
        updatedTask.setDescription("Practice Mockito");
        updatedTask.setStatus(TaskStatus.INPROGRESS);
        updatedTask.setPriority(TaskPriority.HIGH);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(existingTask));

        when(taskRepository.save(existingTask))
                .thenReturn(existingTask);

        Task result = taskService.updateTask(1L, updatedTask);

        assertEquals("Learn Advanced Spring Boot", result.getTitle());
        assertEquals("Practice Mockito", result.getDescription());
        assertEquals(TaskStatus.INPROGRESS, result.getStatus());

        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskDoesNotExist() {
        Task updatedTask = createSampleTask();

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(999L, updatedTask));
    }

    @Test
    void deleteTask_ShouldDeleteExistingTask() {
        Task task = createSampleTask();

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(999L));

        assertTrue(exception.getMessage().contains("999"));
    }
}