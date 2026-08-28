package com.learn.todoapp.service;

import com.learn.todoapp.dto.TodoRequest;
import com.learn.todoapp.dto.TodoResponse;

import java.util.List;

public interface TodoService {

    TodoResponse createTodo(TodoRequest todoRequest);
    List<TodoResponse> getAllTodos();
    TodoResponse getTodoById(Long Id);
    TodoResponse updateTodo(Long Id, TodoRequest todoRequest);
    void deleteTodo(Long Id);
}
