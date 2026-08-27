package com.learn.schoolsystem.result;

import com.learn.schoolsystem.examination.Exam;
import com.learn.schoolsystem.student.Student;
import com.learn.schoolsystem.subject.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double marks;
    private String grade;

    @ManyToOne
    @JoinColumn(name = "student_id",  nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    public Result() {
    }
}
