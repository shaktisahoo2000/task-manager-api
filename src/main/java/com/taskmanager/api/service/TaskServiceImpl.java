package com.taskmanager.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.exception.TaskNotFoundException;
import com.taskmanager.api.repository.TaskRepository;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Create a new task
    @Override
    public Task createTask(Task task) {

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }

        return taskRepository.save(task);
    }

    // Retrieve all tasks
    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Retrieve a task by ID
    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {

        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id: " + id));
    }

    // Update an existing task
    @Override
    public Task updateTask(Long id, Task task) {

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }

        Task existingTask = getTaskById(id);

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());

        return taskRepository.save(existingTask);
    }

    // Delete a task
    @Override
    public void deleteTask(Long id) {

        Task existingTask = getTaskById(id);
        taskRepository.delete(existingTask);
    }
}