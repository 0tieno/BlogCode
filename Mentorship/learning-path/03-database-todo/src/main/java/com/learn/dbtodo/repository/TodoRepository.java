package com.learn.dbtodo.repository;

import com.learn.dbtodo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Repository — the database access layer
//
// You write THIS:
//   public interface TodoRepository extends JpaRepository<Todo, Long> {}
//
// Spring generates ALL of this SQL for you:
//   findAll()     → SELECT * FROM todos
//   findById(id)  → SELECT * FROM todos WHERE id = ?
//   save(todo)    → INSERT INTO todos ... (if new) or UPDATE ... (if existing)
//   delete(todo)  → DELETE FROM todos WHERE id = ?
//   count()       → SELECT COUNT(*) FROM todos
//   existsById(1) → SELECT 1 FROM todos WHERE id = 1
//
// JpaRepository<Todo, Long> means:
//   - Todo = the entity type this repository manages
//   - Long = the type of the primary key (id is Long)
//
// You also write CUSTOM query methods just by naming them correctly:
//   findByTitle(String title)          → SELECT * FROM todos WHERE title = ?
//   findByCompleted(boolean completed) → SELECT * FROM todos WHERE completed = ?
//   findByTitleContaining(String s)    → SELECT * FROM todos WHERE title LIKE '%s%'
// ══════════════════════════════════════════════════════════════════════════════
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // Spring generates this SQL: SELECT * FROM todos WHERE completed = ?
    List<Todo> findByCompleted(boolean completed);
}

