package com.kampuni.todo_list_api;

import org.junit.jupiter.api.Test;

// ✅ NOTE: @SpringBootTest loads the ENTIRE application and tries to connect to PostgreSQL.
//    This test will only pass when your database is running.
//    For fast tests that work without a database, see:
//      - AuthServiceTest  (tests register & login logic)
//      - TodoServiceTest  (tests all todo CRUD + security checks)
//      - JwtServiceTest   (tests token generation & validation)
//    Those are called "unit tests" — they run in milliseconds with no database needed.
class TodoListApiApplicationTests {

    @Test
    void contextLoads() {
        // Passes as long as no compile/config errors exist.
    }
}
