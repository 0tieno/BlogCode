package com.kampuni.todo_list_api.repository;

import com.kampuni.todo_list_api.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ✅ FIXED: JpaRepository<ToDo, Long> — entity is ToDo, not TodoRepository.
@Repository
public interface TodoRepository extends JpaRepository<ToDo, Long> {
    // Spring auto-generates: SELECT * FROM todos WHERE user_id = ?
    // ✅ BEST PRACTICE: parameter names should be camelCase (userId not UserId)
    List<ToDo> findByUserId(Long userId);
}
