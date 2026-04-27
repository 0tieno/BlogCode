package com.kampuni.userapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/*
    rep our database table
*/


@Entity
@Table(name = "usersdb")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;
    private int age;

    public User() {
    }

    public User(String email, String name, int age) {
        this.email = email;
        this.name = name;
        this.age = age;
    }
}
