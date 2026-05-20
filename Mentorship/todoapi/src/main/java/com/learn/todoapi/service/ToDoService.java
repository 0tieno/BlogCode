package com.learn.todoapi.service;


import com.learn.todoapi.dto.ToDoRequest;
import com.learn.todoapi.dto.ToDoResponse;
import com.learn.todoapi.entity.ToDo;
import com.learn.todoapi.exception.TodoNotFoundException;
import com.learn.todoapi.repository.ToDoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ToDoService {

   private final ToDoRepository toDoRepository;

    public ToDoService(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    public Page<ToDoResponse> getAll(Pageable pageable){
        return toDoRepository
                .findAll(pageable)
                .map(this::toDoResponse);
    }

    public ToDoResponse getById(Long id){
        return toDoRepository
                .findById(id)
                .map(this::toDoResponse)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @Transactional
    public ToDoResponse create(ToDoRequest toDoRequest){
        ToDo toDo = new ToDo();

        toDo.setTitle(toDoRequest.title());
        toDo.setDescription(toDoRequest.description());
        ToDo saved = toDoRepository.save(toDo);

        return toDoResponse(saved);
    }

    @Transactional
    public ToDoResponse update(Long id, ToDoRequest toDoRequest){
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        toDo.setTitle(toDoRequest.title());
        toDo.setDescription(toDoRequest.description());
        return toDoResponse(toDoRepository.save(toDo));
    }

    @Transactional
    public ToDoResponse toggleComplete(Long id){
        ToDo toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        toDo.setCompleted(!toDo.isCompleted());
        return toDoResponse(toDoRepository.save(toDo));
    }

    @Transactional
    public boolean delete(Long id){
        if (toDoRepository.existsById(id)){
            toDoRepository.deleteById(id);
            return true;
        }
        return false;
    }


    //MAPPER - PRIVATE HELPER METHOD: converts internal entity → response DTO
private ToDoResponse toDoResponse(ToDo toDo){
        return new ToDoResponse(
                toDo.getId(),
                toDo.getTitle(),
                toDo.getDescription(),
                toDo.isCompleted(),
                toDo.getCreatedAt()
        );
}
}
