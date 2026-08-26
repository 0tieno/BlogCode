package com.learn.spring101;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student,Integer> {
    public List<Student> findAllByFirstnameContaining(String firstname);
}
