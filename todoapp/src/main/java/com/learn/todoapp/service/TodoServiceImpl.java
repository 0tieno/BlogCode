package com.learn.todoapp.service;


import com.learn.todoapp.dto.TodoRequest;
import com.learn.todoapp.dto.TodoResponse;
import com.learn.todoapp.entity.Todo;
import com.learn.todoapp.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoResponse createTodo(TodoRequest todoRequest) {

        Todo todo = new Todo();

        todo.setTitle(todoRequest.title());
        todo.setDescription(todoRequest.description());
        todo.setCompleted(false);

        Todo savedTodo = todoRepository.save(todo);

        return new TodoResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getDescription(),
                savedTodo.isCompleted()
        );
    }

    @Override
    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAll()
                .stream()
                .map(todo -> new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.isCompleted()
                ))
                .toList();
    }

    @Override
    public TodoResponse getTodoById(Long Id) {
        Todo todo = todoRepository.findById(Id).orElse(null);
        assert todo != null;
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted()
        );
    }

    @Override
    public TodoResponse updateTodo(Long Id, TodoRequest todoRequest) {

        Todo todo = todoRepository.findById(Id).orElse(null);

        assert todo != null;
        todo.setTitle(todoRequest.title());
        todo.setDescription(todoRequest.description());

        Todo updateTodo = todoRepository.save(todo);
        return new TodoResponse(
                updateTodo.getId(),
                updateTodo.getTitle(),
                updateTodo.getDescription(),
                updateTodo.isCompleted()
        );
    }

    @Override
    public void deleteTodo(Long Id) {
        todoRepository.deleteById(Id);
    }
}
