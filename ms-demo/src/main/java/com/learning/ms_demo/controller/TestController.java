package com.learning.ms_demo.controller;

import com.learning.ms_demo.Student;
import com.learning.ms_demo.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {

    private final StudentRepository studentRepository;

    public TestController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping("/students")
    public Student postStudent(@RequestBody Student student){
        return studentRepository.save(student);
    }

    @GetMapping("/students")
    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    @GetMapping("/students/{student-id}")
    public Student getStudentById(@PathVariable("student-id") Integer Id){
        return studentRepository.findById(Id).orElse(null);
    }

    @GetMapping("/students/search/{student-name}")
    public List<Student> getStudentByFirstName(@PathVariable("student-name") String name){
        return studentRepository.findAllByFirstnameContaining(name);
    }



}
