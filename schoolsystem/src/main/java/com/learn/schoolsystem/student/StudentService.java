package com.learn.schoolsystem.student;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest createStudentRequest);
    StudentResponse getStudent(Long id);
    List<StudentResponse> getStudents();
    List<StudentResponse> searchStudents(String name);
    StudentResponse updateStudent(Long id, CreateStudentRequest createStudentRequest);
    void deleteStudent(Long id);
}
