package org.onlinetestsystem.onlinetest.service;

import org.onlinetestsystem.onlinetest.entity.*;
import org.onlinetestsystem.onlinetest.repository.StudentAnswerRepository;
import org.onlinetestsystem.onlinetest.repository.TestRepository;
import org.onlinetestsystem.onlinetest.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    private static final Logger logger = LoggerFactory.getLogger(TestService.class);

    // Create a new test
    public Test createTest(Test test) {
        logger.info("Creating a new test: {}", test);
        return testRepository.save(test);
    }

    // Get all tests
    public List<Test> getAllTests() {
        logger.info("Fetching all tests.");
        return testRepository.findAll();
    }

    // Fetch tests by lecturer
    public List<Test> getTestsByLecturer(Users lecturer) {
        logger.info("Fetching tests for lecturer: {}", lecturer.getFullName());
        return testRepository.findByLecturer(lecturer);
    }

    // Get available tests
    public List<Test> getAvailableTests() {
        logger.info("Fetching available tests.");
        return testRepository.findAll();  // Can be filtered for availability if needed
    }

    // Get test by ID
    public Test getTestById(Long testId) {
        logger.info("Fetching test with ID: {}", testId);
        return testRepository.findById(testId).orElseThrow(() -> {
            logger.error("Test not found with ID: {}", testId);
            return new RuntimeException("Test not found");
        });
    }

    // Submit student's test answers
    public void submitTestAnswers(StudentAnswer studentAnswer) {
        logger.info("Submitting answers for student: {} on test: {}", studentAnswer.getStudent().getFullName());
        studentAnswerRepository.save(studentAnswer);
        logger.info("Successfully saved answers for student: {}", studentAnswer.getStudent().getFullName());
    }

    // Calculate the test result
    public TestResult calculateTestResult(StudentAnswer studentAnswer) {
        logger.info("Calculating test result for student: {}", studentAnswer.getStudent().getFullName());

        Test test = studentAnswer.getTest();
        List<Question> questions = test.getQuestions();
        List<Integer> selectedAnswers = studentAnswer.getSelectedAnswers();

        // Check if questions or selected answers are empty
        if (questions.isEmpty()) {
            logger.error("Test questions are empty.");
            throw new IllegalArgumentException("Test questions are empty.");
        }

        if (selectedAnswers.isEmpty()) {
            logger.error("Student answers are empty.");
            throw new IllegalArgumentException("Student answers are empty.");
        }

        // Ensure the number of questions matches the number of selected answers
        if (questions.size() != selectedAnswers.size()) {
            logger.error("Number of questions does not match the number of selected answers.");
            throw new IllegalArgumentException("Number of questions does not match the number of selected answers.");
        }

        int score = 0;

        // Calculate the score
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Integer studentAnswerIndex = selectedAnswers.get(i);

            // Handle grading based on question type
            if (question.getQuestionType() == TestType.MULTIPLE_CHOICE) {
                // For multiple choice, compare selected answer with correctAnswerIndices
                if (question.getCorrectAnswerIndices() != null && question.getCorrectAnswerIndices().contains(studentAnswerIndex)) {
                    score++;
                    logger.debug("Question {} is correct (Multiple Choice).", i);
                } else {
                    logger.debug("Question {} is incorrect (Multiple Choice).", i);
                }
            } else if (question.getQuestionType() == TestType.TRUE_FALSE) {
                // For true/false, compare the Boolean value of the answer
                Boolean studentBooleanAnswer = studentAnswerIndex == 1;  // Assuming 1 represents True and 0 represents False
                if (question.getCorrectAnswer().equals(studentBooleanAnswer)) {
                    score++;
                    logger.debug("Question {} is correct (True/False).", i);
                } else {
                    logger.debug("Question {} is incorrect (True/False).", i);
                }
            } else {
                logger.warn("Unknown question type for question {}: {}", i, question.getQuestionType());
            }
        }

        // Create and return the TestResult object
        TestResult result = new TestResult();
        result.setStudent(studentAnswer.getStudent());
        result.setTest(test);
        result.setScore(score);
        result.setPassed(score >= (questions.size() * 0.5));  // Assuming 50% pass mark

        logger.info("Test result calculated: Score = {}, Passed = {}", score, result.isPassed());

        return result;
    }

    // Save the TestResult after it is calculated
    public void saveTestResult(TestResult result) {
        logger.info("Saving test result for student: {}", result.getStudent().getFullName());
        testResultRepository.save(result);
        logger.info("Test result saved successfully for student: {}", result.getStudent().getFullName());
    }

    // Get results for a student
    public List<TestResult> getResultsForStudent(Long studentId) {
        logger.info("Fetching test results for student ID: {}", studentId);
        return testResultRepository.findByStudentId(studentId);
    }

    public boolean isTestAvailable(Test test) {
        LocalDateTime now = LocalDateTime.now();

        // Use startDate and endDate directly, since they are already LocalDateTime
        LocalDateTime startDate = test.getStartDate(); // No parsing needed
        LocalDateTime endDate = test.getEndDate();     // No parsing needed

        // Test is available if the current time is after the start date and before the end date
        return (startDate == null || now.isAfter(startDate)) && (endDate == null || now.isBefore(endDate));
    }

    public void autoSubmitTest(StudentAnswer studentAnswer) {
        Test test = studentAnswer.getTest();

        // Use startDate directly, since it's LocalDateTime
        LocalDateTime testStartTime = test.getStartDate();

        // Assuming the test has a duration in minutes
        int duration = test.getDuration(); // Example: duration in minutes

        // Calculate when the test should end based on the start time and duration
        LocalDateTime testEndTime = testStartTime.plusMinutes(duration);

        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // If the current time is after the test end time, auto-submit the test
        if (now.isAfter(testEndTime)) {
            // Log the auto submission process
            logger.info("Auto-submitting test for student: {} on test: {}",
                    studentAnswer.getStudent().getFullName(), test.getName());

            // Logic to save the student's answer and auto-submit the test
            submitTestAnswers(studentAnswer);

            // Calculate and save the test result
            TestResult result = calculateTestResult(studentAnswer);
            saveTestResult(result);

            // Mark the test as auto-submitted
            studentAnswer.setSubmitted(true);
            studentAnswer.setSubmissionTime(now);  // Record the submission time
            logger.info("Test auto-submitted for student: {} on test: {}",
                    studentAnswer.getStudent().getFullName(), test.getName());
        } else {
            logger.info("Test is still in progress for student: {} on test: {}",
                    studentAnswer.getStudent().getFullName(), test.getName());
        }
    }


}
