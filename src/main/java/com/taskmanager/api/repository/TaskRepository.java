package com.taskmanager.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {
	
	 List<Task> findByTitleContainingIgnoreCase(String title);
	 
	 List<Task> findByStatus(TaskStatus status);

}
