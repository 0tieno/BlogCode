package com.learn.jpa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id1;
    private Integer id;
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;
    private int age;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false)
    private LocalDateTime lastModified;

    @ManyToMany(mappedBy = "authors")
    private List<Course> courses;
}
