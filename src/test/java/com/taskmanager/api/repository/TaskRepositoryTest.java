package com.taskmanager.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.TaskPriority;
import com.taskmanager.api.entity.TaskStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void saveTask_ShouldReturnSavedTask() {

        Task task = new Task();
        task.setTitle("Learn Spring Boot");
        task.setDescription("Complete REST API project");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);

        Task savedTask = taskRepository.save(task);

        assertNotNull(savedTask.getId());
        assertEquals("Learn Spring Boot", savedTask.getTitle());
        assertEquals(TaskStatus.TODO, savedTask.getStatus());
        assertEquals(TaskPriority.HIGH, savedTask.getPriority());
        assertNotNull(savedTask.getCreatedAt());
        assertNotNull(savedTask.getUpdatedAt());
    }

    @Test
    void findById_ShouldReturnTask() {

        Task task = createTask();
        Task savedTask = taskRepository.save(task);

        Optional<Task> result = taskRepository.findById(savedTask.getId());

        assertTrue(result.isPresent());
        assertEquals("Learn Java", result.get().getTitle());
    }

    @Test
    void findAll_ShouldReturnAllTasks() {

        taskRepository.deleteAll();

        Task task1 = createTask();
        task1.setTitle("Learn Java");

        Task task2 = createTask();
        task2.setTitle("Learn Hibernate");

        taskRepository.saveAll(List.of(task1, task2));

        List<Task> tasks = taskRepository.findAll();

        assertEquals(2, tasks.size());
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {

        Task task = taskRepository.save(createTask());

        task.setTitle("Learn Advanced Java");
        task.setStatus(TaskStatus.COMPLETED);

        Task updatedTask = taskRepository.save(task);

        assertEquals("Learn Advanced Java", updatedTask.getTitle());
        assertEquals(TaskStatus.COMPLETED, updatedTask.getStatus());
    }

    @Test
    void deleteTask_ShouldRemoveTask() {

        Task task = taskRepository.save(createTask());
        Long taskId = task.getId();

        taskRepository.deleteById(taskId);

        Optional<Task> result = taskRepository.findById(taskId);

        assertFalse(result.isPresent());
    }

    private Task createTask() {

        Task task = new Task();
        task.setTitle("Learn Java");
        task.setDescription("Practice Java concepts");
        task.setStatus(TaskStatus.INPROGRESS);
        task.setPriority(TaskPriority.MEDIUM);

        return task;
    }
}