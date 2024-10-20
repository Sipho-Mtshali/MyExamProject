package org.onlinetestsystem.onlinetest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Users student;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @ElementCollection
    private List<Integer> selectedAnswers = new ArrayList<>();  // Store answers as index values from the options

    // New fields to track submission status and time
    private boolean submitted;  // Whether the test has been submitted

    private LocalDateTime submissionTime;  // The time whe
}
