package com.learning.ms_student_application.service;

import com.learning.ms_student_application.student.Student;

import java.util.List;

public interface StudentService {

    Student save(Student s);

    List<Student> findAllStudents();

    Student findStudentByEmail(String email);

    Student update(Student s);

    void delete (String email);
}
