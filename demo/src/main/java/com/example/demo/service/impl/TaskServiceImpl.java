package com.example.demo.service.impl;

import com.example.demo.domain.CreateTaskRequest;
import com.example.demo.domain.entity.Task;
import com.example.demo.domain.entity.TaskStatus;
import com.example.demo.repository.TaskRespository;
import com.example.demo.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRespository taskRespository;

    public TaskServiceImpl(TaskRespository taskRespository) {
        this.taskRespository = taskRespository;
    }


    @Override
    public Task createTask(CreateTaskRequest request) {
        Instant now = Instant.now();

        Task task = new Task(
                null,
                request.title(),
                request.description(),
                request.dueDate(),
                TaskStatus.OPEN,
                request.priority(),
                now,
                now
        );
        return taskRespository.save(task);
    }
}
