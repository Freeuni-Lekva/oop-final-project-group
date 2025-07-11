package dao;

import models.FillInTheBlankQuestion;
import models.Question;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.*;

public class FillInTheBlankDaoTest {
    private Connection connection;
    private FillInTheBlankDao fillInTheBlankDao;
    private int testQuizId;
    private FillInTheBlankQuestion fillInTheBlankQuestion;
    private FillInTheBlankQuestion fillInTheBlankQuestion2;
    private int userId;


    //Sets up the new quiz for the testing purposes
    @BeforeEach
    public void setUp() throws SQLException {
        DatabaseSetup.run();
        connection = DatabaseConnection.getConnection();

        // Clear relevant tables before each test
        try (Statement clearStmt = connection.createStatement()) {
            clearStmt.execute("DELETE FROM FillInBlankAnswers");
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

        fillInTheBlankDao = new FillInTheBlankDao();
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
        fillInTheBlankDao.addQuestion(fillInTheBlankQuestion);
        fillInTheBlankDao.addQuestion(fillInTheBlankQuestion2);

        int questionId = fillInTheBlankQuestion.getQuestionId();
        int questionId2 = fillInTheBlankQuestion2.getQuestionId();

        FillInTheBlankQuestion question = (FillInTheBlankQuestion)fillInTheBlankDao.getQuestionById(questionId);
        assertEquals(question, fillInTheBlankQuestion);

        FillInTheBlankQuestion question2 = (FillInTheBlankQuestion)fillInTheBlankDao.getQuestionById(questionId2);
        assertEquals(question2, fillInTheBlankQuestion2);

        List<Question> questionList = fillInTheBlankDao.getAllQuestions(testQuizId);
        assertEquals(2, questionList.size());
        assertEquals(question, questionList.get(0));
        assertEquals(question2, questionList.get(1));
    }

    //Tests if the functions return false, null, etc. when they fail
    @Test
    public void testErrors(){
        //Setting up a question
        String questionText = "The capital of Georgia is _____ and it was founded by _____.";
        ArrayList<HashSet<String>> answers = new ArrayList<>();
        answers.add(new HashSet<>());
        answers.add(new HashSet<>());
        answers.get(0).add("Tbilisi");
        answers.get(1).add("Vakhtang Gorgasali");
        answers.get(1).add("Vakhtang I Gorgasali");
        answers.get(1).add("Gorgasali");

        //addQuestion Test
        fillInTheBlankQuestion = new FillInTheBlankQuestion(questionText, answers, Integer.MAX_VALUE, 0, 2);
        assertFalse(fillInTheBlankDao.addQuestion(fillInTheBlankQuestion));

        //getQuestionById Test
        assertNull(fillInTheBlankDao.getQuestionById(Integer.MAX_VALUE));

        //deleteQuestionTest
        assertFalse(fillInTheBlankDao.deleteQuestion(fillInTheBlankQuestion.getQuestionId()));

        //getAllQuestionsTest
        List<Question> questionList = fillInTheBlankDao.getAllQuestions(testQuizId);
        assertTrue(questionList.isEmpty());

        //updateQuestionTest
        assertFalse(fillInTheBlankDao.updateQuestion(fillInTheBlankQuestion));
    }

    //tests deleteQuestion
    @Test
    public void testDeleteQuestion(){
        defineQuestions();
        assertTrue(fillInTheBlankDao.addQuestion(fillInTheBlankQuestion));
        assertTrue(fillInTheBlankDao.addQuestion(fillInTheBlankQuestion2));
        assertTrue(fillInTheBlankDao.deleteQuestion(fillInTheBlankQuestion.getQuestionId()));
        assertNull(fillInTheBlankDao.getQuestionById(fillInTheBlankQuestion.getQuestionId()));
        assertTrue(fillInTheBlankDao.deleteQuestion(fillInTheBlankQuestion2.getQuestionId()));
        assertNull(fillInTheBlankDao.getQuestionById(fillInTheBlankQuestion2.getQuestionId()));
        List<Question> questionList = fillInTheBlankDao.getAllQuestions(testQuizId);
        assertTrue(questionList.isEmpty());
    }

    //tests updateQuestion
    @Test
    public void testUpdateQuestion(){
        defineQuestions();
        assertTrue(fillInTheBlankDao.addQuestion(fillInTheBlankQuestion));
        fillInTheBlankQuestion = new FillInTheBlankQuestion(fillInTheBlankQuestion.getQuestionId(),
                fillInTheBlankQuestion2.getQuestionText(),
                fillInTheBlankQuestion2.getCorrectAnswers(),
                fillInTheBlankQuestion2.getQuizId(),
                fillInTheBlankQuestion2.getOrderInQuiz(),
                fillInTheBlankQuestion2.getMaxScore());
        assertTrue(fillInTheBlankDao.updateQuestion(fillInTheBlankQuestion));
        Question q = fillInTheBlankDao.getQuestionById(fillInTheBlankQuestion.getQuestionId());
        assertEquals(fillInTheBlankQuestion, q);
    }

    //Creates 2 questions used in tests
    private void defineQuestions(){
        String questionText = "The capital of Georgia is _____ and it was founded by _____.";
        ArrayList<HashSet<String>> answers = new ArrayList<>();
        answers.add(new HashSet<>());
        answers.add(new HashSet<>());
        answers.get(0).add("Tbilisi");
        answers.get(1).add("Vakhtang Gorgasali");
        answers.get(1).add("Vakhtang I Gorgasali");
        answers.get(1).add("Gorgasali");
        fillInTheBlankQuestion = new FillInTheBlankQuestion(questionText, answers, testQuizId, 0, 1);

        String questionText2 = "The Sun rises in the _____ and sets in the _____.";
        ArrayList<HashSet<String>> answers2 = new ArrayList<>();
        answers2.add(new HashSet<>());
        answers2.add(new HashSet<>());
        answers2.get(0).add("east");
        answers2.get(0).add("East");
        answers2.get(1).add("West");
        answers2.get(1).add("west");
        fillInTheBlankQuestion2 = new FillInTheBlankQuestion(questionText2, answers2, testQuizId, 1, 2);
    }
}