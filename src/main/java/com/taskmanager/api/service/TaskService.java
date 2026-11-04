package com.taskmanager.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taskmanager.api.dto.TaskRequest;
import com.taskmanager.api.dto.TaskResponse;
import com.taskmanager.api.entity.TaskStatus;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    List<TaskResponse> getAllTasks();

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, TaskRequest request);

    void deleteTask(Long id);
    
    List<TaskResponse> searchTasksByTitle(String title);
    
    List<TaskResponse> filterTasksByStatus(TaskStatus status);
    
    Page<TaskResponse> getAllTasks(Pageable pageable);
}