package com.learning.ms_demo.repository;

import com.learning.ms_demo.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Integer> {
}
