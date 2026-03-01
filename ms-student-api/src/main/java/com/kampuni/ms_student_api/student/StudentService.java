package com.kampuni.ms_student_api.student;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    public List<Student> getAllStudents(){
        return List.of(
                new Student(
                        1L,
                        "john doe",
                        "john@gmail.com",
                        22
                )
        );
    }

}
