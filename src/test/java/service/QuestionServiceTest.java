package service;

import dao.DatabaseConnection;
import models.FillInTheBlankQuestion;
import models.MultipleChoiceQuestion;
import models.Question;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.List;


public class QuestionServiceTest {
    private Connection connection;
    private QuestionService questionService;
    private int testQuizId;
    private int userId;
    private int multipleChoiceQuestionId;
    private int fillInBlankQuestionId;
    private static final String QUESTION_TEXT_1 = "The capital of Georgia is _____.";
    private static final String QUESTION_TEXT_2 = "Which countries are in Europe?";

    @BeforeEach
    public void setUp() throws SQLException {
        connection = DatabaseConnection.getConnection();
        questionService = new QuestionService();

        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Users (username, email, password_hash, salt) " +
                        "VALUES ('testuser', 'test@example.com', 'testhash', 'testsalt')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        userId = keys.getInt(1);

        stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title) " +
                        "SELECT user_id, 'Test Quiz' FROM Users WHERE username = 'testuser'",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        testQuizId = keys.getInt(1);

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

        stmt = connection.prepareStatement(
                "INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (?, ?, ?)"
        );
        stmt.setInt(1, multipleChoiceQuestionId);
        stmt.setString(2, "Germany");
        stmt.setBoolean(3, true);
        stmt.executeUpdate();

        stmt.setInt(1, multipleChoiceQuestionId);
        stmt.setString(2, "Brazil");
        stmt.setBoolean(3, false);
        stmt.executeUpdate();

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

    //Cleans up all the tables
    @AfterEach
    public void cleanUp() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?");
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    //Tests get all questions from quiz
    @Test
    public void testGetAllQuestionsFromQuiz(){
        List<Question> questions = questionService.getAllQuestionsFromQuiz(testQuizId);
        assertEquals(2, questions.size());
        assertInstanceOf(MultipleChoiceQuestion.class, questions.get(0));
        assertInstanceOf(FillInTheBlankQuestion.class, questions.get(1));
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) questions.get(0);
        FillInTheBlankQuestion fillInTheBlankQuestion = (FillInTheBlankQuestion) questions.get(1);
        assertEquals(fillInBlankQuestionId, fillInTheBlankQuestion.getQuestionId());
        assertEquals(multipleChoiceQuestionId, multipleChoiceQuestion.getQuestionId());
        assertEquals(testQuizId, fillInTheBlankQuestion.getQuizId());
        assertEquals(testQuizId, multipleChoiceQuestion.getQuizId());
        assertEquals(QUESTION_TEXT_1, fillInTheBlankQuestion.getQuestionText());
        assertEquals(QUESTION_TEXT_2, multipleChoiceQuestion.getQuestionText());
    }

    //Tests if the wrong quiz id was specified
    @Test
    public void testError(){
        List<Question> questions = questionService.getAllQuestionsFromQuiz(Integer.MAX_VALUE);
        assertTrue(questions.isEmpty());
        assertNull(questionService.getQuestionById(Integer.MAX_VALUE));
    }

    //Tests empty quiz
    @Test
    public void testEmptyQuiz() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title) VALUES (?, 'Empty Quiz')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        int emptyQuizId = keys.getInt(1);

        List<Question> questions = questionService.getAllQuestionsFromQuiz(emptyQuizId);
        assertTrue(questions.isEmpty());
    }

    //Test getQuestionById method
    @Test
    public void testGetQuestionById(){
        Question retrievedQuestion = questionService.getQuestionById(multipleChoiceQuestionId);
        assertInstanceOf(MultipleChoiceQuestion.class, retrievedQuestion);
        assertEquals(multipleChoiceQuestionId, retrievedQuestion.getQuestionId());
        assertEquals(testQuizId, retrievedQuestion.getQuizId());
        assertEquals(QUESTION_TEXT_2, retrievedQuestion.getQuestionText());

        Question retrievedQuestion2 = questionService.getQuestionById(fillInBlankQuestionId);
        assertInstanceOf(FillInTheBlankQuestion.class, retrievedQuestion2);
        assertEquals(fillInBlankQuestionId, retrievedQuestion2.getQuestionId());
        assertEquals(testQuizId, retrievedQuestion2.getQuizId());
        assertEquals(QUESTION_TEXT_1, retrievedQuestion2.getQuestionText());
    }
}
