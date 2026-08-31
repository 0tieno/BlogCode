package com.learn.jpa.model;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity {
    private LocalDateTime lastCreatedAt;
    private LocalDateTime lastModifiedAt;
    private String createdBy;
    private String lastModifiedBy;
}
