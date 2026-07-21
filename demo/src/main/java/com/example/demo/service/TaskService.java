package com.example.demo.service;

import com.example.demo.domain.CreateTaskRequest;
import com.example.demo.domain.entity.Task;

public interface TaskService {
    Task createTask(CreateTaskRequest request);
}
