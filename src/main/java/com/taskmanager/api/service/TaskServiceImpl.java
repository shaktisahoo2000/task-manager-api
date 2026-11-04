package com.taskmanager.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanager.api.dto.TaskRequest;
import com.taskmanager.api.dto.TaskResponse;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.TaskStatus;
import com.taskmanager.api.exception.TaskNotFoundException;
import com.taskmanager.api.repository.TaskRepository;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskResponse createTask(TaskRequest request) {

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());

        Task savedTask = taskRepository.save(task);

        return convertToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id: " + id));

        return convertToResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id: " + id));

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setStatus(request.getStatus());
        existingTask.setPriority(request.getPriority());

        Task updatedTask = taskRepository.save(existingTask);

        return convertToResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id: " + id));

        taskRepository.delete(existingTask);
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasksByTitle(String title) {

        return taskRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    

    // Entity to Response DTO conversion
    private TaskResponse convertToResponse(Task task) {

        TaskResponse response = new TaskResponse();

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return response;
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> filterTasksByStatus(TaskStatus status) {

        return taskRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {

        return taskRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

}