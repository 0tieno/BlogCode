package com.learn.schoolsystem.classroom;

import com.learn.schoolsystem.student.Student;
import com.learn.schoolsystem.teacher.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "classrooms")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    private String grade;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher classTeacher;

    @OneToMany(mappedBy = "classroom")
    private List<Student> students = new ArrayList<>();

    public Classroom() {
    }
}