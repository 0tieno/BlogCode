-- =============================================================================
-- data.sql - sample data loaded automatically at application startup.
--
-- Spring Boot's SQL initializer executes this script against the H2
-- in-memory database every time the application starts (see
-- spring.sql.init.mode=always in application.properties), after Hibernate
-- has created the "student" table from the Student entity (see
-- spring.jpa.defer-datasource-initialization=true). This gives every
-- student running this project the exact same starting data set, with zero
-- manual setup required.
--
-- H2 syntax note: standard ANSI SQL INSERT statements work unmodified
-- against H2, which is why this script is portable and easy to read.
-- =============================================================================

INSERT INTO student (first_name, last_name, email, course, age) VALUES
    ('John', 'Doe', 'john.doe@example.com', 'Computer Science', 21),
    ('Jane', 'Smith', 'jane.smith@example.com', 'Mathematics', 22),
    ('Alice', 'Johnson', 'alice.johnson@example.com', 'Computer Science', 20),
    ('Bob', 'Williams', 'bob.williams@example.com', 'Physics', 23),
    ('Charlie', 'Brown', 'charlie.brown@example.com', 'Mathematics', 24),
    ('Diana', 'Miller', 'diana.miller@example.com', 'Computer Science', 19),
    ('Ethan', 'Davis', 'ethan.davis@example.com', 'Physics', 22),
    ('Fiona', 'Garcia', 'fiona.garcia@example.com', 'Biology', 21);
