package org.onlinetestsystem.onlinetest.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @Enumerated(EnumType.STRING)
    private TestType questionType;  // Add this field to define question type

    @ElementCollection
    private List<String> answerOptions;

    @ElementCollection
    private List<Integer> correctAnswerIndices;

    private Boolean correctAnswer;  // For True/False

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
}
