package dao;

import models.MultipleChoiceWithMultipleAnswersQuestion;
import models.Question;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.*;

public class MultipleChoiceWithMultipleAnswersDaoTest {
    private Connection connection;
    private MultipleChoiceWithMultipleAnswersDao multipleChoiceWithMultipleAnswersDao;
    private int testQuizId;
    private MultipleChoiceWithMultipleAnswersQuestion multipleChoiceWithMultipleAnswersQuestion;
    private MultipleChoiceWithMultipleAnswersQuestion multipleChoiceWithMultipleAnswersQuestion2;
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

        multipleChoiceWithMultipleAnswersDao = new MultipleChoiceWithMultipleAnswersDao();
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

    //Tests addQuestion, getQuestionBtId and getAllQuestions
    @Test
    public void testAddGet(){
        defineQuestions();
        assertTrue(multipleChoiceWithMultipleAnswersDao.addQuestion(multipleChoiceWithMultipleAnswersQuestion));
        assertTrue(multipleChoiceWithMultipleAnswersDao.addQuestion(multipleChoiceWithMultipleAnswersQuestion2));

        int questionId = multipleChoiceWithMultipleAnswersQuestion.getQuestionId();
        int questionId2 = multipleChoiceWithMultipleAnswersQuestion2.getQuestionId();

        MultipleChoiceWithMultipleAnswersQuestion question = (MultipleChoiceWithMultipleAnswersQuestion) multipleChoiceWithMultipleAnswersDao.getQuestionById(questionId);
        assertEquals(multipleChoiceWithMultipleAnswersQuestion, question);

        MultipleChoiceWithMultipleAnswersQuestion question2 = (MultipleChoiceWithMultipleAnswersQuestion) multipleChoiceWithMultipleAnswersDao.getQuestionById(questionId2);
        assertEquals(multipleChoiceWithMultipleAnswersQuestion2, question2);

        List<Question> questionList = multipleChoiceWithMultipleAnswersDao.getAllQuestions(testQuizId);
        assertEquals(2, questionList.size());
        assertEquals(multipleChoiceWithMultipleAnswersQuestion, questionList.get(0));
        assertEquals(multipleChoiceWithMultipleAnswersQuestion2, questionList.get(1));
    }

    //Tests if the functions return false, null, etc. when they fail
    @Test
    public void testErrors(){
        //Setting up a question
        String questionText = "Which of these countries are located in Europe?";
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Germany", true);
        answers.put("Brazil", false);
        answers.put("France", true);
        answers.put("Japan", false);
        answers.put("Spain", true);

        //addQuestion Test
        multipleChoiceWithMultipleAnswersQuestion = new MultipleChoiceWithMultipleAnswersQuestion(questionText, answers, Integer.MAX_VALUE, 0);
        assertFalse(multipleChoiceWithMultipleAnswersDao.addQuestion(multipleChoiceWithMultipleAnswersQuestion));

        //getQuestionById Test
        assertNull(multipleChoiceWithMultipleAnswersDao.getQuestionById(Integer.MAX_VALUE));

        //deleteQuestionTest
        assertFalse(multipleChoiceWithMultipleAnswersDao.deleteQuestion(multipleChoiceWithMultipleAnswersQuestion.getQuestionId()));

        //getAllQuestionsTest
        List<Question> questionList = multipleChoiceWithMultipleAnswersDao.getAllQuestions(testQuizId);
        assertTrue(questionList.isEmpty());

        //updateQuestionTest
        assertFalse(multipleChoiceWithMultipleAnswersDao.updateQuestion(multipleChoiceWithMultipleAnswersQuestion));
    }

    //tests deleteQuestion
    @Test
    public void testDeleteQuestion(){
        defineQuestions();
        assertTrue(multipleChoiceWithMultipleAnswersDao.addQuestion(multipleChoiceWithMultipleAnswersQuestion));
        assertTrue(multipleChoiceWithMultipleAnswersDao.addQuestion(multipleChoiceWithMultipleAnswersQuestion2));
        assertTrue(multipleChoiceWithMultipleAnswersDao.deleteQuestion(multipleChoiceWithMultipleAnswersQuestion.getQuestionId()));
        assertNull(multipleChoiceWithMultipleAnswersDao.getQuestionById(multipleChoiceWithMultipleAnswersQuestion.getQuestionId()));
        assertTrue(multipleChoiceWithMultipleAnswersDao.deleteQuestion(multipleChoiceWithMultipleAnswersQuestion2.getQuestionId()));
        assertNull(multipleChoiceWithMultipleAnswersDao.getQuestionById(multipleChoiceWithMultipleAnswersQuestion2.getQuestionId()));
        List<Question> questionList = multipleChoiceWithMultipleAnswersDao.getAllQuestions(testQuizId);
        assertTrue(questionList.isEmpty());
    }

    //tests updateQuestion
    @Test
    public void testUpdateQuestion(){
        defineQuestions();
        assertTrue(multipleChoiceWithMultipleAnswersDao.addQuestion(multipleChoiceWithMultipleAnswersQuestion));
        multipleChoiceWithMultipleAnswersQuestion = new MultipleChoiceWithMultipleAnswersQuestion(multipleChoiceWithMultipleAnswersQuestion.getQuestionId(),
                multipleChoiceWithMultipleAnswersQuestion2.getQuestionText(),
                multipleChoiceWithMultipleAnswersQuestion2.getOptions(),
                multipleChoiceWithMultipleAnswersQuestion2.getQuizId(),
                multipleChoiceWithMultipleAnswersQuestion2.getOrderInQuiz());
        assertTrue(multipleChoiceWithMultipleAnswersDao.updateQuestion(multipleChoiceWithMultipleAnswersQuestion));
        Question q = multipleChoiceWithMultipleAnswersDao.getQuestionById(multipleChoiceWithMultipleAnswersQuestion.getQuestionId());
        assertEquals(multipleChoiceWithMultipleAnswersQuestion, q);
    }

    //Creates 2 questions used in tests
    private void defineQuestions(){
        String questionText = "Which of these countries are located in Europe?";
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Germany", true);
        answers.put("Brazil", false);
        answers.put("France", true);
        answers.put("Japan", false);
        answers.put("Spain", true);
        multipleChoiceWithMultipleAnswersQuestion = new MultipleChoiceWithMultipleAnswersQuestion(questionText, answers, testQuizId, 0);

        String questionText2 = "Which of these countries are NOT located in Europe?";
        Map<String, Boolean> answers2 = new HashMap<>();
        answers2.put("Germany", false);
        answers2.put("Brazil", true);
        answers2.put("France", false);
        answers2.put("Japan", true);
        answers2.put("Spain", false);
        multipleChoiceWithMultipleAnswersQuestion2 = new MultipleChoiceWithMultipleAnswersQuestion(questionText2, answers2, testQuizId, 1);
    }
}