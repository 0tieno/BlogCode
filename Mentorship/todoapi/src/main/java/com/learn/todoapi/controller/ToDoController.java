package com.learn.todoapi.controller;


import com.learn.todoapi.dto.ToDoRequest;
import com.learn.todoapi.dto.ToDoResponse;
import com.learn.todoapi.service.ToDoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @GetMapping
    public ResponseEntity<Page<ToDoResponse>> getAll(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)Pageable pageable
            ){
        return ResponseEntity.ok(toDoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDoResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(toDoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ToDoResponse> create(@Valid @RequestBody ToDoRequest toDoRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDoService.create(toDoRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDoResponse> update(@PathVariable Long id, @Valid @RequestBody ToDoRequest toDoRequest){
        return ResponseEntity.ok(toDoService.update(id, toDoRequest));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ToDoResponse> toggleComplete(@PathVariable Long id){
        return ResponseEntity.ok(toDoService.toggleComplete(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        return toDoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
