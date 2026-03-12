package com.learning.ms_student_application.student;


import com.learning.ms_student_application.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student save(@RequestBody Student student){
        return studentService.save(student);
    }

    @GetMapping("/{email}")
    public Student findByEmail(@PathVariable("email") String email){
        return studentService.findStudentByEmail(email);
    }

    @GetMapping
     public List<Student> findAllStudents(){
         return studentService.findAllStudents();
     }

     @PutMapping
    public Student updateStudent(@RequestBody Student student){
        return studentService.update(student);
     }

     @DeleteMapping("/{email}")
    public void deleteStudent(@PathVariable("email") String email){
        studentService.delete(email);
     }
}
