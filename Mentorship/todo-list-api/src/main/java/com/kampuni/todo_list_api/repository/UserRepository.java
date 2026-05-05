package com.kampuni.todo_list_api.repository;

import com.kampuni.todo_list_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// ✅ FIXED: JpaRepository<User, Long> — the first type param is the ENTITY, not the repository itself.
//    The second type param is the type of the primary key (id is Long).
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA generates the SQL for this automatically from the method name.
    // "findBy" + "Email" → SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}
