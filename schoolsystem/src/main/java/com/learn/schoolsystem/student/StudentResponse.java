package com.learn.schoolsystem.student;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class StudentResponse {
    private Long id;
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private Long classroomId;
    private String classroomName;
}
