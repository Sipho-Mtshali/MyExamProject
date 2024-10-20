package org.onlinetestsystem.onlinetest.service;

import org.onlinetestsystem.onlinetest.entity.Question;
import org.onlinetestsystem.onlinetest.entity.Test;
import org.onlinetestsystem.onlinetest.entity.TestType;
import org.onlinetestsystem.onlinetest.repository.QuestionRepository;
import org.onlinetestsystem.onlinetest.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestRepository testRepository;

    public void addQuestionToTest(Long testId, Question question) {
        Optional<Test> test = testRepository.findById(testId);
        if (test.isPresent()) {
            TestType questionType = question.getQuestionType();

            // Handle the logic based on question type
            if (questionType == TestType.MULTIPLE_CHOICE) {
                if (question.getCorrectAnswerIndices() == null || question.getCorrectAnswerIndices().isEmpty()) {
                    throw new RuntimeException("At least one correct answer must be provided for multiple-choice questions.");
                }
            } else if (questionType == TestType.TRUE_FALSE) {
                if (question.getCorrectAnswer() == null) {
                    throw new RuntimeException("A correct answer must be provided for true/false questions.");
                }
            }

            // Associate the question with the test
            question.setTest(test.get());
            questionRepository.save(question);
        } else {
            throw new RuntimeException("Test not found with ID: " + testId);
        }
    }
}