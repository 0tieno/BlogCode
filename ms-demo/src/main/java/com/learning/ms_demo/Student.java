package com.learning.ms_demo;


import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private String firstname;

    private String lastname;

    @Column(unique = true)
    private String email;

    private int age;

    @OneToOne(
            mappedBy = "student",
            cascade = CascadeType.ALL
    )

    private StudentProfile studentProfile;

    @ManyToOne
    @JoinColumn(name = "school_id")

    @JsonBackReference
    private School school;

    public Student() {
    }

    public Student(String firstname, String lastname, String email, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.age = age;
    }
}
