package com.learning.ms_student_application.service.impl;

import com.learning.ms_student_application.service.StudentService;
import com.learning.ms_student_application.student.InMemoryStudentDao;
import com.learning.ms_student_application.student.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InMemoryStudentService implements StudentService {

    private final InMemoryStudentDao dao;

    public InMemoryStudentService(InMemoryStudentDao dao) {
        this.dao = dao;
    }

    @Override
    public Student save(Student s) {
        return dao.save(s);
    }

    @Override
    public List<Student> findAllStudents() {
        return dao.findAllStudents();
    }

    @Override
    public Student findStudentByEmail(String email) {
        return dao.findStudentByEmail(email);
    }

    @Override
    public Student update(Student s) {
        return dao.update(s);
    }

    @Override
    public void delete(String email) {
        dao.delete(email);
    }
}
