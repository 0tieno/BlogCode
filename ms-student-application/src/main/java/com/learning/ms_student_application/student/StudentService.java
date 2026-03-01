package com.learning.ms_student_application.student;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {
    public List<Student> findAllStudents(){
        return List.of(
                new Student(
                        "Ali",
                        "Abdul",
                        LocalDate.now(),
                        "ali@gmail.com",
                        34
                ),
                new Student(
                        "ron",
                        "Abdul",
                        LocalDate.now(),
                        "ali@gmail.com",
                        34
                )
        );
    }
}
