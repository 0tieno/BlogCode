package com.learning.ms_demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class StudentProfile {

    @Setter
    @Getter
    @Id
    @GeneratedValue

    private Integer id;

    @Setter
    @Getter
    private String bio;

    @OneToOne
    @JoinColumn(
            name = "student_id"
    )
    private Student student;

    public StudentProfile() {
    }

    public StudentProfile(String bio) {
        this.bio = bio;
    }

}
