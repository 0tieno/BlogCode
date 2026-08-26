package com.learn.spring101;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @GetMapping("/students/{student-id}")
    public Student getStudent(@PathVariable("student-id") Integer Id){
        return studentRepository.findById(Id).orElse(null);
    }

    @GetMapping("/students")
    public List<Student> getStudents(){
        return studentRepository.findAll();
    }

    @GetMapping("/students/search/{student-name}")
        public List<Student> getStudentByName(@PathVariable("student-name") String name){
            return studentRepository.findAllByFirstnameContaining(name);
        }


    @DeleteMapping("/students/{student-id}")
    public void deleteStudent(@PathVariable("student-id") Integer Id){
        studentRepository.deleteById(Id);
    }
}
