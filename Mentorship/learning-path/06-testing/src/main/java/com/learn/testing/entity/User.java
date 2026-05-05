package com.learn.testing.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Table(name="users") @Getter @Setter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(unique=true, nullable=false) private String email;
    @Column(nullable=false) private String password;
    public User() {}
    public User(String name, String email, String password) { this.name=name; this.email=email; this.password=password; }
}

