package com.learning.ms_demo;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class School {

    @Id
    @GeneratedValue

    private Integer id;

    private String name;

    @OneToMany(
            mappedBy = "school"
    )

    @JsonManagedReference
    private List<Student> students;

    public School() {
    }

    public School(List<Student> students) {
        this.students = students;
    }

    public School(String name, List<Student> students) {
        this.name = name;
        this.students = students;
    }

}
