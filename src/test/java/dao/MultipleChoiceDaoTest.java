package dao;

import models.MultipleChoiceQuestion;
import models.Question;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.*;

public class MultipleChoiceDaoTest {
    private Connection connection;
    private MultipleChoiceDao multipleChoiceDao;
    private int testQuizId;
    private MultipleChoiceQuestion multipleChoiceQuestion;
    private MultipleChoiceQuestion multipleChoiceQuestion2;
    private int userId;

    //Sets up the new quiz for the testing purposes
    @BeforeEach
    public void setUp() throws SQLException {
        DatabaseSetup.run();
        connection = DatabaseConnection.getConnection();

        // Clear relevant tables before each test
        try (Statement clearStmt = connection.createStatement()) {
            clearStmt.execute("DELETE FROM AnswerOptionsMC");
            clearStmt.execute("DELETE FROM Questions");
            clearStmt.execute("DELETE FROM Quizzes");
            clearStmt.execute("DELETE FROM Users");
        }

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
        keys.close();
        stmt.close();

        multipleChoiceDao = new MultipleChoiceDao();
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

    //Tests addQuestion, getQuestionById and getAllQuestions
    @Test
    public void testAddGet(){
        defineQuestions();
        assertTrue(multipleChoiceDao.addQuestion(multipleChoiceQuestion));
        assertTrue(multipleChoiceDao.addQuestion(multipleChoiceQuestion2));

        int questionId = multipleChoiceQuestion.getQuestionId();
        int questionId2 = multipleChoiceQuestion2.getQuestionId();

        MultipleChoiceQuestion question = (MultipleChoiceQuestion) multipleChoiceDao.getQuestionById(questionId);
        assertEquals(multipleChoiceQuestion, question);

        MultipleChoiceQuestion question2 = (MultipleChoiceQuestion) multipleChoiceDao.getQuestionById(questionId2);
        assertEquals(multipleChoiceQuestion2, question2);

        List<Question> questionList = multipleChoiceDao.getAllQuestions(testQuizId);
        assertEquals(2, questionList.size());
        assertEquals(multipleChoiceQuestion, questionList.get(0));
        assertEquals(multipleChoiceQuestion2, questionList.get(1));
    }

    //Tests if the functions return false, null, etc. when they fail
    @Test
    public void testErrors(){
        //Setting up a question
        String questionText = "What is the capital of France?";
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Paris", true);
        answers.put("London", false);
        answers.put("Berlin", false);
        answers.put("Madrid", false);

        //addQuestion Test
        multipleChoiceQuestion = new MultipleChoiceQuestion(questionText, answers, Integer.MAX_VALUE, 0, 1);
        assertFalse(multipleChoiceDao.addQuestion(multipleChoiceQuestion));

        //getQuestionById Test
        assertNull(multipleChoiceDao.getQuestionById(Integer.MAX_VALUE));

        //deleteQuestionTest
        assertFalse(multipleChoiceDao.deleteQuestion(multipleChoiceQuestion.getQuestionId()));

        //getAllQuestionsTest
        List<Question> questionList = multipleChoiceDao.getAllQuestions(testQuizId);
        assertTrue(questionList.isEmpty());

        //updateQuestionTest
        assertFalse(multipleChoiceDao.updateQuestion(multipleChoiceQuestion));
    }

    //tests deleteQuestion
    @Test
    public void testDeleteQuestion(){
        defineQuestions();
        assertTrue(multipleChoiceDao.addQuestion(multipleChoiceQuestion));
        assertTrue(multipleChoiceDao.addQuestion(multipleChoiceQuestion2));
        assertTrue(multipleChoiceDao.deleteQuestion(multipleChoiceQuestion.getQuestionId()));
        assertNull(multipleChoiceDao.getQuestionById(multipleChoiceQuestion.getQuestionId()));
        assertTrue(multipleChoiceDao.deleteQuestion(multipleChoiceQuestion2.getQuestionId()));
        assertNull(multipleChoiceDao.getQuestionById(multipleChoiceQuestion2.getQuestionId()));
        List<Question> questionList = multipleChoiceDao.getAllQuestions(testQuizId);
        assertTrue(questionList.isEmpty());
    }

    //tests updateQuestion
    @Test
    public void testUpdateQuestion(){
        defineQuestions();
        assertTrue(multipleChoiceDao.addQuestion(multipleChoiceQuestion));
        multipleChoiceQuestion = new MultipleChoiceQuestion(multipleChoiceQuestion.getQuestionId(),
                multipleChoiceQuestion2.getQuestionText(),
                multipleChoiceQuestion2.getOptions(),
                multipleChoiceQuestion2.getQuizId(),
                multipleChoiceQuestion2.getOrderInQuiz(),
                multipleChoiceQuestion2.getMaxScore());
        assertTrue(multipleChoiceDao.updateQuestion(multipleChoiceQuestion));
        Question q = multipleChoiceDao.getQuestionById(multipleChoiceQuestion.getQuestionId());
        assertEquals(multipleChoiceQuestion, q);
    }

    //Creates 2 questions used in tests
    private void defineQuestions(){
        String questionText = "What is the capital of France?";
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Paris", true);
        answers.put("London", false);
        answers.put("Berlin", false);
        answers.put("Madrid", false);
        multipleChoiceQuestion = new MultipleChoiceQuestion(questionText, answers, testQuizId, 0, 1);

        String questionText2 = "What is the capital of Germany?";
        Map<String, Boolean> answers2 = new HashMap<>();
        answers2.put("Paris", false);
        answers2.put("London", false);
        answers2.put("Berlin", true);
        answers2.put("Madrid", false);
        multipleChoiceQuestion2 = new MultipleChoiceQuestion(questionText2, answers2, testQuizId, 1, 2);
    }
}