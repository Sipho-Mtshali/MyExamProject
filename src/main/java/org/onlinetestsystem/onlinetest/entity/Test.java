package org.onlinetestsystem.onlinetest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private int duration;  // Duration in minutes

    @Enumerated(EnumType.STRING)
    private TestType testType;  // Test type (e.g., "True/False" or "Multiple Choice")

    private int testAttempts;  // Number of attempts allowed

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Question> questions;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")  // This column will hold the ID of the lecturer who created the test
    private Users lecturer;

    private LocalDateTime startDate;  // Change to LocalDateTime
    private LocalDateTime endDate;    // Change to LocalDateTime
}


