package com.taskmanager.api.dto;

import java.time.LocalDateTime;

import com.taskmanager.api.entity.TaskPriority;
import com.taskmanager.api.entity.TaskStatus;

import lombok.Data;

@Data
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}