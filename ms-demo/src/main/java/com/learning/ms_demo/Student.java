package com.learning.ms_demo;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_students")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "c_firstname")
    private String firstname;

    private String lastname;

    @Column(unique = true)
    private String email;

    private int age;

    public Student() {
    }

    public Student(String firstname, String lastname, String email, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.age = age;
    }
}
