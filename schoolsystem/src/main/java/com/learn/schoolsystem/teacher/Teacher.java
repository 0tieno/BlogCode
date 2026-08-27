package com.learn.schoolsystem.teacher;

import com.learn.schoolsystem.classroom.Classroom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "classTeacher",cascade = CascadeType.ALL)
    private List<Classroom> classrooms = new ArrayList<>();

    public Teacher() {
    }
}