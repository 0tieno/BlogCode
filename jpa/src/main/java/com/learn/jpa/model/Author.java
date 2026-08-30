package com.learn.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "")
public class Author {

    @Id
    private Long id1;
    private Integer id;
    private String firstName;
    private String lastName;
    private int age;
}
