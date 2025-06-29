package dao;

import models.MultipleChoiceQuestion;
import models.Question;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.*;

public class MultipleChoiceDaoTest {
    private Connection connection;
    private dao.MultipleChoiceDao multipleChoiceDao;
    private int testQuizId;
    private MultipleChoiceQuestion multipleChoiceQuestion;
    private MultipleChoiceQuestion multipleChoiceQuestion2;

    //Sets up the new quiz for the testing purposes
    @BeforeEach
    public void setUp() throws SQLException {
        connection = DatabaseConnection.getConnection();
        connection.prepareStatement(
                "INSERT IGNORE INTO Users (username, email, password_hash, salt) " +
                        "VALUES ('testuser', 'test@example.com', 'testhash', 'testsalt')"
        ).executeUpdate();

        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title) " +
                        "SELECT user_id, 'Test Quiz' FROM Users WHERE username = 'testuser'",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        testQuizId = keys.getInt(1);
        keys.close();
        stmt.close();

        multipleChoiceDao = new dao.MultipleChoiceDao();
    }

    //Cleans up all the tables
    @AfterEach
    public void cleanUp() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Quizzes WHERE quiz_id = ?");
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    //Tests addQuestion, getQuestionBtId and getAllQuestions
    @Test
    public void testAddGet(){
        defineQuestions();
        assertTrue(multipleChoiceDao.addQuestion(multipleChoiceQuestion));
        assertTrue(multipleChoiceDao.addQuestion(multipleChoiceQuestion2));

        int questionId = multipleChoiceQuestion.getQuestionId();
        int questionId2 = multipleChoiceQuestion2.getQuestionId();

        MultipleChoiceQuestion question = (MultipleChoiceQuestion)multipleChoiceDao.getQuestionById(questionId);
        assertEquals(question, multipleChoiceQuestion);

        MultipleChoiceQuestion question2 = (MultipleChoiceQuestion)multipleChoiceDao.getQuestionById(questionId2);
        assertEquals(question2, multipleChoiceQuestion2);

        List<Question> questionList = multipleChoiceDao.getAllQuestions(testQuizId);
        assertEquals(2, questionList.size());
        assertEquals(question, questionList.get(0));
        assertEquals(question2, questionList.get(1));
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
        multipleChoiceQuestion = new MultipleChoiceQuestion(questionText, answers, Integer.MAX_VALUE, 0);
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
                multipleChoiceQuestion2.getOrderInQuiz());
        assertTrue(multipleChoiceDao.updateQuestion(multipleChoiceQuestion));
        Question q = multipleChoiceDao.getQuestionById(multipleChoiceQuestion.getQuestionId());
        assertEquals(multipleChoiceQuestion, q);
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
        multipleChoiceQuestion = new MultipleChoiceQuestion(questionText, answers, testQuizId, 0);

        String questionText2 = "Which of these countries are NOT located in Europe?";
        Map<String, Boolean> answers2 = new HashMap<>();
        answers2.put("Germany", false);
        answers2.put("Brazil", true);
        answers2.put("France", false);
        answers2.put("Japan", true);
        answers2.put("Spain", false);
        multipleChoiceQuestion2 = new MultipleChoiceQuestion(questionText2, answers2, testQuizId, 1);
    }
}