package dao;

import models.UserAnswer;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserAnswerDaoTest {
    private Connection connection;
    private UserAnswerDao userAnswerDao;
    private UserAnswer multipleChoiceAnswer;
    private UserAnswer fillInTheBlankAnswer;
    private int userId;

    //Sets up all the tables in the database for testing
    @BeforeEach
    public void setUp() throws SQLException {
        connection = DatabaseConnection.getConnection();
        userAnswerDao = new UserAnswerDao();

        // Create test user
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Users (username, email, password_hash, salt) " +
                        "VALUES ('newtestuser', 'newtest@example.com', 'testhash', 'testsalt')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        userId = keys.getInt(1);

        // Create test quiz
        stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title) VALUES (?, 'Test Quiz')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int quizId = keys.getInt(1);

        // Create test quiz attempt
        stmt = connection.prepareStatement(
                "INSERT INTO UserQuizAttempts (user_id, quiz_id, start_time) VALUES (?, ?, CURRENT_TIMESTAMP)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.setInt(2, quizId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int attemptId = keys.getInt(1);

        // Create multiple choice question
        stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'Which is correct?', 'MULTIPLE_CHOICE', 1)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, quizId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int multipleChoiceQuestionId = keys.getInt(1);

        // Create answer option for multiple choice
        stmt = connection.prepareStatement(
                "INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, multipleChoiceQuestionId);
        stmt.setString(2, "Correct Answer");
        stmt.setBoolean(3, true);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int optionId = keys.getInt(1);

        // Create fill-in-blank question
        stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'The capital is _____', 'FILL_IN_BLANK', 2)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, quizId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int fillInBlankQuestionId = keys.getInt(1);

        multipleChoiceAnswer = new UserAnswer(attemptId, multipleChoiceQuestionId, "Correct Answer", optionId, true);
        fillInTheBlankAnswer = new UserAnswer(attemptId,fillInBlankQuestionId, " Answer", null, false);

        keys.close();
        stmt.close();
    }

    //Cleans up all the databases
    @AfterEach
    public void cleanUp() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?");
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    @Test

    //Tests SaveAnswer, getUserAnswerById and getAnswersForAttempt
    public void testAddGet(){
        //Testing saveAnswer
        assertTrue(userAnswerDao.saveAnswer(multipleChoiceAnswer));
        assertTrue(userAnswerDao.saveAnswer(fillInTheBlankAnswer));

        //Testing getUserAnswerById
        int userAnswerId1 = multipleChoiceAnswer.getUserAnswerId();
        int userAnswerId2 = fillInTheBlankAnswer.getUserAnswerId();
        UserAnswer retrievedUserAnswer = userAnswerDao.getAnswerById(userAnswerId1);
        UserAnswer retrievedUserAnswer2 = userAnswerDao.getAnswerById(userAnswerId2);
        assertEquals(multipleChoiceAnswer, retrievedUserAnswer);
        assertEquals(fillInTheBlankAnswer, retrievedUserAnswer2);

        //Testing getAnswersForAttempt
        List<UserAnswer> userAnswers = new ArrayList<>();
        userAnswers.add(retrievedUserAnswer);
        userAnswers.add(retrievedUserAnswer2);
        assertEquals(userAnswers, userAnswerDao.getAnswersForAttempt(multipleChoiceAnswer.getAttemptId()));
    }

    //Tests updateAnswers
    @Test
    public void testUpdateAnswer(){
        assertTrue(userAnswerDao.saveAnswer(multipleChoiceAnswer));
        assertTrue(userAnswerDao.saveAnswer(fillInTheBlankAnswer));
        UserAnswer userAnswer = new UserAnswer(multipleChoiceAnswer.getUserAnswerId(), fillInTheBlankAnswer.getAttemptId(), fillInTheBlankAnswer.getQuestionId(),
                fillInTheBlankAnswer.getAnswerGivenText(), fillInTheBlankAnswer.getSelectedOptionId(), fillInTheBlankAnswer.isCorrect());
        userAnswerDao.updateAnswer(userAnswer);
        UserAnswer retrievedAnswer = userAnswerDao.getAnswerById(multipleChoiceAnswer.getUserAnswerId());
        assertEquals(fillInTheBlankAnswer.getAnswerGivenText(), retrievedAnswer.getAnswerGivenText());
        assertEquals(fillInTheBlankAnswer.getSelectedOptionId(), retrievedAnswer.getSelectedOptionId());
        assertEquals(fillInTheBlankAnswer.isCorrect(), retrievedAnswer.isCorrect());
        assertEquals(fillInTheBlankAnswer.getAttemptId(), retrievedAnswer.getAttemptId());
        assertEquals(fillInTheBlankAnswer.getQuestionId(), retrievedAnswer.getQuestionId());
        assertNotEquals(fillInTheBlankAnswer.getUserAnswerId(), retrievedAnswer.getUserAnswerId());
    }

    //Tests deleteAnswersForQuestion
    @Test
    public void testDeleteAnswersForQuestion() {
        assertTrue(userAnswerDao.saveAnswer(multipleChoiceAnswer));
        assertTrue(userAnswerDao.saveAnswer(fillInTheBlankAnswer));
        assertEquals(2, userAnswerDao.getAnswersForAttempt(multipleChoiceAnswer.getAttemptId()).size());

        // Delete answers for one question
        assertTrue(userAnswerDao.deleteAnswersForQuestion(
                multipleChoiceAnswer.getAttemptId(),
                multipleChoiceAnswer.getQuestionId()
        ));

        // Verify only one answer remains
        List<UserAnswer> remainingAnswers = userAnswerDao.getAnswersForAttempt(multipleChoiceAnswer.getAttemptId());
        assertEquals(1, remainingAnswers.size());
        assertEquals(fillInTheBlankAnswer.getQuestionId(), remainingAnswers.get(0).getQuestionId());

        // Test deleting non-existent answers
        assertFalse(userAnswerDao.deleteAnswersForQuestion(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    //Tests errors
    @Test
    public void testErrors(){
        //Testing saveAnswers
        UserAnswer invalidAttempt = new UserAnswer(Integer.MAX_VALUE, fillInTheBlankAnswer.getQuestionId(), "Answer", null, true);
        assertFalse(userAnswerDao.saveAnswer(invalidAttempt));
        UserAnswer invalidQuestion = new UserAnswer(fillInTheBlankAnswer.getAttemptId(), Integer.MAX_VALUE, "Answer", null, true);
        assertFalse(userAnswerDao.saveAnswer(invalidQuestion));
        UserAnswer invalidOption = new UserAnswer(multipleChoiceAnswer.getAttemptId(), multipleChoiceAnswer.getQuestionId(), "Answer", Integer.MAX_VALUE, true);
        assertFalse(userAnswerDao.saveAnswer(invalidOption));

        //Testing getUserAnswerById
        assertNull(userAnswerDao.getAnswerById(Integer.MAX_VALUE));

        //Testing getAnswersForAttempt
        UserAnswer nonExistentAnswer = new UserAnswer(123456, fillInTheBlankAnswer.getAttemptId(), fillInTheBlankAnswer.getQuestionId(), "Answer", null, true);
        assertFalse(userAnswerDao.updateAnswer(nonExistentAnswer));
        UserAnswer invalidUpdate = new UserAnswer(fillInTheBlankAnswer.getUserAnswerId(), Integer.MAX_VALUE, fillInTheBlankAnswer.getQuestionId(), "Answer", null, true);
        assertFalse(userAnswerDao.updateAnswer(invalidUpdate));

        //Tests updateAnswers
        List<UserAnswer> emptyList = userAnswerDao.getAnswersForAttempt(Integer.MAX_VALUE);
        assertTrue(emptyList.isEmpty());
    }
}
