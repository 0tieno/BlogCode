package com.kampuni.ms_student_api.student;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Student {
    private Long id;
    private String name;
    private String email;
    private int age;

    public Student() {
    }

    public Student(Long id, String name, String email, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}