package service;

import dao.DatabaseConnection;
import models.*;
import org.junit.jupiter.api.*;
import quiz_engine.QuizSession;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizAttemptServiceTest {
    private Connection connection;
    private QuizAttemptService quizAttemptService;
    private int userId;
    private int testQuizId;
    private int multipleChoiceQuestionId;
    private int fillInBlankQuestionId;

    //Sets up tables for tests
    @BeforeEach
    public void setUp() throws SQLException {
        connection = DatabaseConnection.getConnection();
        quizAttemptService = new QuizAttemptService();

        // Create test user
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Users (username, email, password_hash, salt) " +
                        "VALUES ('testuser', 'test@example.com', 'testhash', 'testsalt')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        userId = keys.getInt(1);

        // Create test quiz
        stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title, description, creation_date, is_random_order, display_type, is_immediate_correction, is_practice_mode_enabled) " +
                        "VALUES (?, 'Test Quiz', 'Test Description', NOW(), false, 'SINGLE_PAGE', false, false)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        testQuizId = keys.getInt(1);

        // Create multiple choice question
        stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'Which countries are in Europe?', 'MULTIPLE_CHOICE', 1)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        multipleChoiceQuestionId = keys.getInt(1);

        // Create answer options for multiple choice
        stmt = connection.prepareStatement(
                "INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, multipleChoiceQuestionId);
        stmt.setString(2, "Germany");
        stmt.setBoolean(3, true);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        keys.getInt(1);

        stmt.setInt(1, multipleChoiceQuestionId);
        stmt.setString(2, "Brazil");
        stmt.setBoolean(3, false);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        keys.getInt(1);

        // Create fill-in-blank question
        stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'The capital of Georgia is _____.', 'FILL_IN_BLANK', 2)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        fillInBlankQuestionId = keys.getInt(1);

        // Create answer for fill-in-blank
        stmt = connection.prepareStatement(
                "INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (?, ?, ?)"
        );
        stmt.setInt(1, fillInBlankQuestionId);
        stmt.setInt(2, 0);
        stmt.setString(3, "Tbilisi");
        stmt.executeUpdate();

        keys.close();
        stmt.close();
    }

    //Cleans up the databases created by the setup method and closes connection
    @AfterEach
    public void cleanUp() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?");
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    // Test successful quiz attempt start
    @Test
    public void testStartQuizAttemptSuccess() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);

        assertNotNull(session);
        assertEquals(testQuizId, session.getQuiz().getQuizId());
        assertEquals(2, session.getQuestions().size());
        assertEquals(multipleChoiceQuestionId, session.getQuestions().get(0).getQuestionId());
        assertEquals(fillInBlankQuestionId, session.getQuestions().get(1).getQuestionId());
        assertEquals(0, session.getCurrentQuestionId());
        assertNotNull(session.getStartTime());
    }

    // Test starting quiz attempt with non-existent quiz
    @Test
    public void testStartQuizAttemptInvalidQuiz() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> quizAttemptService.startQuizAttempt(userId, Integer.MAX_VALUE)
        );
        assertEquals("This Quiz Id does not exist!", exception.getMessage());
    }

    // Test starting quiz attempt with empty quiz (no questions)
    @Test
    public void testStartQuizAttemptEmptyQuiz() throws SQLException {
        // Create quiz with no questions
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title) VALUES (?, 'Empty Quiz')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        int emptyQuizId = keys.getInt(1);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> quizAttemptService.startQuizAttempt(userId, emptyQuizId)
        );
        assertEquals("Could not create Quiz Attempt!", exception.getMessage());

        keys.close();
        stmt.close();
    }

    // Test successful answer submission
    @Test
    public void testSubmitAnswerSuccess() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit answer for multiple choice question (question order 0)
        List<String> mcAnswers = new ArrayList<>();
        mcAnswers.add("Germany");
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, mcAnswers));

        // Submit answer for fill-in-blank question (question order 1)
        List<String> fibAnswers = new ArrayList<>();
        fibAnswers.add("Tbilisi");
        assertTrue(quizAttemptService.submitAnswer(attemptId, 1, fibAnswers));

        // Verify answers were saved by checking database
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM UserAnswers WHERE attempt_id = ?")) {
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(2, rs.getInt(1)); // Should have 2 answer records
        } catch (SQLException e) {
            fail("Database query failed: " + e.getMessage());
        }
    }

    // Test answer submission with null/empty answers
    @Test
    public void testSubmitAnswerNullEmpty() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Test null answers
        assertFalse(quizAttemptService.submitAnswer(attemptId, 0, null));

        // Test empty answers
        assertFalse(quizAttemptService.submitAnswer(attemptId, 0, new ArrayList<>()));
    }

    // Test answer submission with invalid attempt ID
    @Test
    public void testSubmitAnswerInvalidAttempt() {
        List<String> answers = new ArrayList<>();
        answers.add("Test Answer");

        assertFalse(quizAttemptService.submitAnswer(Integer.MAX_VALUE, 0, answers));
    }

    // Test answer submission with invalid question order
    @Test
    public void testSubmitAnswerInvalidQuestionOrder() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        List<String> answers = new ArrayList<>();
        answers.add("Test Answer");

        // Test question order out of bounds
        assertFalse(quizAttemptService.submitAnswer(attemptId, 999, answers));
        assertFalse(quizAttemptService.submitAnswer(attemptId, -1, answers));
    }

    // Test replacing answers for the same question
    @Test
    public void testSubmitAnswerReplacement() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit initial answer
        List<String> initialAnswers = new ArrayList<>();
        initialAnswers.add("Germany");
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, initialAnswers));

        // Submit replacement answer
        List<String> newAnswers = new ArrayList<>();
        newAnswers.add("Brazil");
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, newAnswers));

        // Verify only one answer record exists for this question
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM UserAnswers WHERE attempt_id = ? AND question_id = ?")) {
            stmt.setInt(1, attemptId);
            stmt.setInt(2, multipleChoiceQuestionId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(1, rs.getInt(1)); // Should have only 1 answer record

            // Verify the answer was updated
            PreparedStatement stmt2 = connection.prepareStatement(
                    "SELECT answer_given_text FROM UserAnswers WHERE attempt_id = ? AND question_id = ?");
            stmt2.setInt(1, attemptId);
            stmt2.setInt(2, multipleChoiceQuestionId);
            ResultSet rs2 = stmt2.executeQuery();
            rs2.next();
            assertEquals("Brazil", rs2.getString("answer_given_text"));
            stmt2.close();
        } catch (SQLException e) {
            fail("Database query failed: " + e.getMessage());
        }
    }

    // Test completing quiz successfully
    @Test
    public void testCompleteQuizSuccess() throws InterruptedException {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit answers for all questions
        List<String> mcAnswers = new ArrayList<>();
        mcAnswers.add("Germany"); // Correct answer
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, mcAnswers));

        List<String> fibAnswers = new ArrayList<>();
        fibAnswers.add("Tbilisi"); // Correct answer
        assertTrue(quizAttemptService.submitAnswer(attemptId, 1, fibAnswers));

        // Wait a moment to ensure time difference
        Thread.sleep(100);

        // Complete the quiz
        QuizAttempt completedAttempt = quizAttemptService.completeQuiz(attemptId);

        assertNotNull(completedAttempt);
        assertEquals(attemptId, completedAttempt.getAttemptId());
        assertEquals(userId, completedAttempt.getUserId());
        assertEquals(testQuizId, completedAttempt.getQuizId());
        assertNotNull(completedAttempt.getStartTime());
        assertNotNull(completedAttempt.getEndTime());
        assertEquals(2, completedAttempt.getScore());
        assertTrue(completedAttempt.getTimeTakenSeconds() > 0);
    }

    // Test completing quiz with partial correct answers
    @Test
    public void testCompleteQuizPartialScore() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit one correct and one incorrect answer
        List<String> mcAnswers = new ArrayList<>();
        mcAnswers.add("Germany"); // Correct answer
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, mcAnswers));

        List<String> fibAnswers = new ArrayList<>();
        fibAnswers.add("Wrong Answer"); // Incorrect answer
        assertTrue(quizAttemptService.submitAnswer(attemptId, 1, fibAnswers));

        // Complete the quiz
        QuizAttempt completedAttempt = quizAttemptService.completeQuiz(attemptId);

        assertNotNull(completedAttempt);
        assertEquals(1, completedAttempt.getScore());
    }

    // Test completing quiz with all wrong answers
    @Test
    public void testCompleteQuizZeroScore() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit incorrect answers
        List<String> mcAnswers = new ArrayList<>();
        mcAnswers.add("Brazil"); // Incorrect answer
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, mcAnswers));

        List<String> fibAnswers = new ArrayList<>();
        fibAnswers.add("Wrong Answer"); // Incorrect answer
        assertTrue(quizAttemptService.submitAnswer(attemptId, 1, fibAnswers));

        // Complete the quiz
        QuizAttempt completedAttempt = quizAttemptService.completeQuiz(attemptId);

        assertNotNull(completedAttempt);
        assertEquals(0, completedAttempt.getScore());
    }

    // Test completing quiz with invalid attempt ID
    @Test
    public void testCompleteQuizInvalidAttempt() {
        QuizAttempt result = quizAttemptService.completeQuiz(Integer.MAX_VALUE);
        assertNull(result);
    }

    // Test completing quiz with no submitted answers
    @Test
    public void testCompleteQuizNoAnswers() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Complete quiz without submitting any answers
        QuizAttempt completedAttempt = quizAttemptService.completeQuiz(attemptId);

        assertNotNull(completedAttempt);
        assertEquals(0, completedAttempt.getScore());
    }

    // Test multiple choice with multiple selections
    @Test
    public void testMultipleChoiceMultipleSelections() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit multiple answers for multiple choice question
        List<String> mcAnswers = new ArrayList<>();
        mcAnswers.add("Germany");
        mcAnswers.add("Brazil");
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, mcAnswers));

        // Complete the quiz
        QuizAttempt completedAttempt = quizAttemptService.completeQuiz(attemptId);

        assertNotNull(completedAttempt);
        assertEquals(0, completedAttempt.getScore()); // All-or-nothing: mixed answers = 0
    }

    // Test partial credit for a single question with multiple parts
    @Test
    public void testPartialCreditSingleQuestion() throws SQLException {
        //Question with 2 blanks
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'The capital of Georgia is _____ and France is _____.', 'FILL_IN_BLANK', 3)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        int twoBlankQuestionId = keys.getInt(1);

        // Add answers for both blanks
        stmt = connection.prepareStatement(
                "INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (?, ?, ?)"
        );
        stmt.setInt(1, twoBlankQuestionId);
        stmt.setInt(2, 0);
        stmt.setString(3, "Tbilisi");
        stmt.executeUpdate();

        stmt.setInt(1, twoBlankQuestionId);
        stmt.setInt(2, 1);
        stmt.setString(3, "Paris");
        stmt.executeUpdate();

        // Now test partial credit
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit only 1 correct answer out of 2 blanks
        List<String> answers = new ArrayList<>();
        answers.add("Tbilisi");      // Correct
        answers.add("Wrong Answer"); // Incorrect
        assertTrue(quizAttemptService.submitAnswer(attemptId, 2, answers)); // Question order 2 (3rd question)
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, Collections.singletonList("Germany")));
        assertTrue(quizAttemptService.submitAnswer(attemptId, 1, Collections.singletonList("Tbilisi")));

        QuizAttempt completedAttempt = quizAttemptService.completeQuiz(attemptId);

        assertNotNull(completedAttempt);
        assertEquals(2.5, completedAttempt.getScore());

        stmt.close();
        keys.close();
    }

    // Test edge case: completing already completed quiz
    @Test
    public void testCompleteAlreadyCompletedQuiz() {
        QuizSession session = quizAttemptService.startQuizAttempt(userId, testQuizId);
        int attemptId = session.getAttemptId();

        // Submit answers and complete once
        List<String> answers = new ArrayList<>();
        answers.add("Germany");
        assertTrue(quizAttemptService.submitAnswer(attemptId, 0, answers));

        QuizAttempt firstCompletion = quizAttemptService.completeQuiz(attemptId);
        assertNotNull(firstCompletion);

        // Try to complete again
        QuizAttempt secondCompletion = quizAttemptService.completeQuiz(attemptId);
        assertNotNull(secondCompletion); // Should still return the attempt
        assertEquals(firstCompletion.getScore(), secondCompletion.getScore());
    }
}