package dao;

import models.*;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QuizDaoTest {
    private QuizDao quizDao;
    private Connection connection;
    private Quiz quiz;
    private Quiz quiz2;
    private Quiz quiz3;
    private int userId;
    private int userId2;

    //Sets up the new user for the testing purposes, creates the connection and quizDao class
    @BeforeEach
    public void setUp() throws SQLException {
        DatabaseSetup.run();
        connection = DatabaseConnection.getConnection();
        quizDao = new QuizDao();

        // Clear relevant tables before each test
        try (Statement clearStmt = connection.createStatement()) {
            clearStmt.execute("DELETE FROM UserAnswers");
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
                "INSERT INTO Users (username, email, password_hash, salt) " +
                        "VALUES ('testuser2', 'test@example.com2', 'testhash2', 'testsalt2')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        userId2 = keys.getInt(1);

        keys.close();
        stmt.close();
    }

    //Cleans up all the tables, closes the connection
    @AfterEach
    public void tearDown() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?");
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        stmt = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?");
        stmt.setInt(1, userId2);
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    @Test
    public void testAddGet(){
        defineQuizzes();
        quizDao.addQuiz(quiz);
        quizDao.addQuiz(quiz2);
        quizDao.addQuiz(quiz3);

        int quizId = quiz.getQuizId();
        int quizId2 = quiz2.getQuizId();
        int quizId3 = quiz3.getQuizId();

        Quiz firstQuiz = quizDao.getQuizById(quizId);
        assertTrue(Math.abs(Timestamp.valueOf(quiz.getCreationDate()).getTime() - Timestamp.valueOf(firstQuiz.getCreationDate()).getTime()) < 2000);

        Quiz secondQuiz = quizDao.getQuizById(quizId2);
        assertTrue(Math.abs(Timestamp.valueOf(quiz2.getCreationDate()).getTime() - Timestamp.valueOf(secondQuiz.getCreationDate()).getTime()) < 2000);

        Quiz thirdQuiz = quizDao.getQuizById(quizId3);
        assertTrue(Math.abs(Timestamp.valueOf(quiz3.getCreationDate()).getTime() - Timestamp.valueOf(thirdQuiz.getCreationDate()).getTime()) < 2000);

        List<Quiz> quizList = quizDao.getAllQuizzes();
        assertEquals(3, quizList.size());
        assertTrue(Math.abs(Timestamp.valueOf(quiz.getCreationDate()).getTime() - Timestamp.valueOf(quizList.get(0).getCreationDate()).getTime()) < 2000);
        assertTrue(Math.abs(Timestamp.valueOf(quiz2.getCreationDate()).getTime() - Timestamp.valueOf(quizList.get(1).getCreationDate()).getTime()) < 2000);
        assertTrue(Math.abs(Timestamp.valueOf(quiz3.getCreationDate()).getTime() - Timestamp.valueOf(quizList.get(2).getCreationDate()).getTime()) < 2000);

        List<Quiz> quizList2 = quizDao.getAllQuizzesByCreator(userId);
        assertEquals(2, quizList2.size());
        assertTrue(Math.abs(Timestamp.valueOf(quiz.getCreationDate()).getTime() - Timestamp.valueOf(quizList2.get(0).getCreationDate()).getTime()) < 2000);
        assertTrue(Math.abs(Timestamp.valueOf(quiz2.getCreationDate()).getTime() - Timestamp.valueOf(quizList2.get(1).getCreationDate()).getTime()) < 2000);
        List<Quiz> quizList3 = quizDao.getAllQuizzesByCreator(userId2);
        assertEquals(1, quizList3.size());
        assertTrue(Math.abs(Timestamp.valueOf(quiz3.getCreationDate()).getTime() - Timestamp.valueOf(quizList3.get(0).getCreationDate()).getTime()) < 2000);
    }

    //tests deleteQuiz
    @Test
    public void testDeleteQuiz(){
        defineQuizzes();
        assertTrue(quizDao.addQuiz(quiz));
        assertTrue(quizDao.addQuiz(quiz2));
        assertTrue(quizDao.addQuiz(quiz3));
        assertTrue(quizDao.deleteQuiz(quiz.getQuizId()));
        assertNull(quizDao.getQuizById(quiz.getQuizId()));
        assertTrue(quizDao.deleteQuiz(quiz2.getQuizId()));
        assertNull(quizDao.getQuizById(quiz2.getQuizId()));
        assertTrue(quizDao.deleteQuiz(quiz3.getQuizId()));
        assertNull(quizDao.getQuizById(quiz3.getQuizId()));
        List<Quiz> QuizList = quizDao.getAllQuizzes();
        assertTrue(QuizList.isEmpty());
    }

    //tests updateQuiz
    @Test
    public void testUpdateQuiz(){
        defineQuizzes();
        assertTrue(quizDao.addQuiz(quiz));
        quiz = new Quiz(quiz.getQuizId(),
                quiz3.getCreatorUserId(),
                quiz3.getTitle(),
                quiz3.getDescription(),
                quiz3.getCreationDate(),
                quiz3.isRandomOrder(),
                quiz3.getQuizDisplayType(),
                quiz3.isImmediateCorrection(),
                quiz3.isPracticeModeEnabled());
        assertTrue(quizDao.updateQuiz(quiz));
        Quiz q = quizDao.getQuizById(quiz.getQuizId());
        assertTrue(Math.abs(Timestamp.valueOf(quiz.getCreationDate()).getTime() - Timestamp.valueOf(q.getCreationDate()).getTime()) < 2000);
    }

    //tests getRecentlyCreatedQuizzes
    @Test
    public void testGetRecentlyCreatedQuizzes(){
        defineQuizzes();
        assertTrue(quizDao.addQuiz(quiz));
        assertTrue(quizDao.addQuiz(quiz2));
        assertTrue(quizDao.addQuiz(quiz3));
        List<Quiz> recentQuizzes = quizDao.getRecentlyCreatedQuizzes(2);
        assertEquals(2, recentQuizzes.size());
        assertTrue(Math.abs(Timestamp.valueOf(quiz3.getCreationDate()).getTime() - Timestamp.valueOf(recentQuizzes.get(0).getCreationDate()).getTime()) < 2000);
        assertTrue(Math.abs(Timestamp.valueOf(quiz2.getCreationDate()).getTime() - Timestamp.valueOf(recentQuizzes.get(1).getCreationDate()).getTime()) < 2000);
        List<Quiz> recentQuizzes2 = quizDao.getRecentlyCreatedQuizzes(4);
        assertEquals(3, recentQuizzes2.size());
        assertTrue(Math.abs(Timestamp.valueOf(quiz.getCreationDate()).getTime() - Timestamp.valueOf(recentQuizzes2.get(2).getCreationDate()).getTime()) < 2000);
    }

    //Tests quizExists
    @Test
    public void testQuizExists(){
        defineQuizzes();
        assertTrue(quizDao.addQuiz(quiz));
        assertTrue(quizDao.addQuiz(quiz2));
        assertTrue(quizDao.addQuiz(quiz3));
        assertTrue(quizDao.quizExists(quiz.getQuizId()));
        assertTrue(quizDao.quizExists(quiz2.getQuizId()));
        assertTrue(quizDao.quizExists(quiz3.getQuizId()));
        assertFalse(quizDao.quizExists(quiz3.getQuizId() + 1));
    }

    //Tests isQuizByCreator
    @Test
    public void testIsQuizCreator(){
        defineQuizzes();
        assertTrue(quizDao.addQuiz(quiz));
        assertTrue(quizDao.addQuiz(quiz2));
        assertTrue(quizDao.addQuiz(quiz3));
        assertTrue(quizDao.isQuizCreator(quiz.getQuizId(), userId));
        assertTrue(quizDao.isQuizCreator(quiz2.getQuizId(), userId));
        assertTrue(quizDao.isQuizCreator(quiz3.getQuizId(), userId2));
        assertFalse(quizDao.isQuizCreator(quiz.getQuizId(), userId2));
        assertFalse(quizDao.isQuizCreator(quiz2.getQuizId(), userId2));
        assertFalse(quizDao.isQuizCreator(quiz3.getQuizId(), userId));
    }

    //Tests getQuizByCreator
    @Test
    public void testGetQuizByCreator(){
        defineQuizzes();
        assertTrue(quizDao.addQuiz(quiz));
        assertTrue(quizDao.addQuiz(quiz2));
        HashMap<String, Boolean> answers = new HashMap<>();
        answers.put("true", true);
        answers.put("false", false);
        Question question = new MultipleChoiceWithMultipleAnswersQuestion("text", answers, quiz.getQuizId(), 1, 1);
        MultipleChoiceWithMultipleAnswersDao multipleChoiceWithMultipleAnswersDAo = new MultipleChoiceWithMultipleAnswersDao();
        multipleChoiceWithMultipleAnswersDAo.addQuestion(question);
        FillInTheBlankQuestion question2 = new FillInTheBlankQuestion("text", new ArrayList<HashSet<String>>(), quiz.getQuizId(), 2, 1);
        FillInTheBlankDao fillInTheBlankDao = new FillInTheBlankDao();
        fillInTheBlankDao.addQuestion(question2);
        assertEquals(2, quizDao.getQuestionCount(quiz.getQuizId()));
        assertEquals(0, quizDao.getQuestionCount(quiz2.getQuizId()));
    }

    //Tests if the functions return false, null, etc. when they fail
    @Test
    public void testErrors(){
        defineQuizzes();
        //AddQuiz
        Quiz testQuiz = new Quiz(0, Integer.MAX_VALUE, "Quiz Test 1", "Description", LocalDateTime.now(), false, QuizDisplayType.SINGLE_PAGE, false, false);
        assertFalse(quizDao.addQuiz(testQuiz));

        //getQuizById
        assertNull(quizDao.getQuizById(Integer.MAX_VALUE));
        assertNull(quizDao.getQuizById(-2));

        //deleteQuiz
        assertFalse(quizDao.deleteQuiz(Integer.MAX_VALUE));
        assertTrue(quizDao.addQuiz(quiz));
        assertTrue(quizDao.deleteQuiz(quiz.getQuizId()));
        assertFalse(quizDao.deleteQuiz(quiz.getQuizId()));

        //updateQuiz
        assertTrue(quizDao.addQuiz(quiz2));
        int originalQuiz2Id = quiz2.getQuizId();
        Quiz invalidQuiz1 = new Quiz(Integer.MAX_VALUE, userId, "Quiz Test", "Description", LocalDateTime.now());
        assertFalse(quizDao.updateQuiz(invalidQuiz1));
        Quiz invalidQuiz2 = new Quiz(quiz.getQuizId(), Integer.MAX_VALUE, "Quiz Test", "Description", LocalDateTime.now());
        assertFalse(quizDao.updateQuiz(invalidQuiz2));
        assertTrue(quizDao.deleteQuiz(originalQuiz2Id));

        //getAllQuizzesByCreator:
        assertEquals(new ArrayList<Quiz>(), quizDao.getAllQuizzesByCreator(Integer.MAX_VALUE));

        //getAllQuizzes
        assertEquals(new ArrayList<Quiz>(), quizDao.getAllQuizzes());

        //getRecentlyCreatedQuizzes
        assertEquals(new ArrayList<Quiz>(), quizDao.getRecentlyCreatedQuizzes(5));
        assertTrue(quizDao.addQuiz(quiz3));
        assertEquals(new ArrayList<Quiz>(), quizDao.getRecentlyCreatedQuizzes(0));
    }

    //Defines the 3 quizzes used in tests
    private void defineQuizzes(){
        quiz = new Quiz(userId, "Quiz Test 1", "Description", LocalDateTime.now());
        quiz2 = new Quiz(userId, "Quiz Test 2", "Description 2", LocalDateTime.now().plusYears(1),
                true, QuizDisplayType.MULTI_PAGE_QUESTION, true, true);
        quiz3 = new Quiz(userId2, "Quiz Test 3", "Description 3", LocalDateTime.now().plusYears(2),
                false, QuizDisplayType.MULTI_PAGE_QUESTION, false, true);
    }
}
