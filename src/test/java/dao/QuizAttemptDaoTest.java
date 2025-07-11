package dao;

import models.QuizAttempt;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class QuizAttemptDaoTest {
    private Connection connection;
    private QuizAttempt quizAttempt;
    private QuizAttempt quizAttempt2;
    private int userId;
    private QuizAttemptDao quizAttemptDao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = DatabaseConnection.getConnection();
        quizAttemptDao = new QuizAttemptDao();

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
        quizAttempt = new QuizAttempt(userId, quizId);

        //Create second testquiz
        stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title) VALUES (?, 'Test Quiz')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int quizId2 = keys.getInt(1);
        quizAttempt2 = new QuizAttempt(userId, quizId2);

        keys.close();
        stmt.close();
    }

    @AfterEach
    public void cleanUp() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?");
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    @Test
    //Tests creating am attempt, getting in by id and getting list of quiz attempts by user id
    public void testAddGet() throws InterruptedException {
        //Testing createAttempt
        assertTrue(quizAttemptDao.createAttempt(quizAttempt));
        Thread.sleep(1000); //Making sure the new quiz is created with a different start time
        assertTrue(quizAttemptDao.createAttempt(quizAttempt2));

        //Testing getAttemptById
        int attemptId = quizAttempt.getAttemptId();
        int attemptId2 = quizAttempt2.getAttemptId();
        QuizAttempt retrievedQuizAttempt = quizAttemptDao.getAttemptById(attemptId);
        QuizAttempt retrievedQuizAttempt2 = quizAttemptDao.getAttemptById(attemptId2);
        assertEquals(quizAttempt, retrievedQuizAttempt);
        assertEquals(quizAttempt2, retrievedQuizAttempt2);
        assertNotNull(retrievedQuizAttempt.getStartTime());
        assertNull(retrievedQuizAttempt.getEndTime());
        assertEquals(0, retrievedQuizAttempt.getTimeTakenSeconds());
        assertEquals(0, retrievedQuizAttempt.getScore());

        //Testing getUserAttempts
        List<QuizAttempt> quizAttempts = new ArrayList<>();
        quizAttempts.add(quizAttempt2);
        quizAttempts.add(quizAttempt);
        assertEquals(quizAttempts, quizAttemptDao.getUserAttempts(userId));
    }

    //Testing completeAttempt and getQuizAttempt methods
    @Test
    public void testCompleteAndGetQuizAttempt() throws InterruptedException {
        //Adding quizAtte,pts
        assertTrue(quizAttemptDao.createAttempt(quizAttempt));
        Thread.sleep(2000); //Making sure the new quiz is created with a different start time
        assertTrue(quizAttemptDao.createAttempt(quizAttempt2));
        Thread.sleep(2000); //Making sure the new quiz is created with a different start time
        QuizAttempt quizAttempt3 = new QuizAttempt(userId, quizAttempt2.getQuizId());
        assertTrue(quizAttemptDao.createAttempt(quizAttempt3));

        //Testing completeAttempt
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt.getAttemptId(), 50));
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt2.getAttemptId(), 150));
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt3.getAttemptId(), 100));
        QuizAttempt retrievedAttempt2 = quizAttemptDao.getAttemptById(quizAttempt2.getAttemptId());
        assertNotNull(retrievedAttempt2.getEndTime());
        assertEquals(150, retrievedAttempt2.getScore());
        assertEquals((int)((retrievedAttempt2.getEndTime().getTime() - retrievedAttempt2.getStartTime().getTime()) / 1000), retrievedAttempt2.getTimeTakenSeconds());
        QuizAttempt retrievedAttempt3 = quizAttemptDao.getAttemptById(quizAttempt3.getAttemptId());
        assertNotNull(retrievedAttempt3.getEndTime());
        assertEquals(100, retrievedAttempt3.getScore());
        assertEquals((int)((retrievedAttempt3.getEndTime().getTime() - retrievedAttempt3.getStartTime().getTime()) / 1000), retrievedAttempt3.getTimeTakenSeconds());

        //Testing getQuizAttempts
        List<QuizAttempt> quizAttempts = new ArrayList<>();
        quizAttempts.add(quizAttempt);
        assertEquals(quizAttempts, quizAttemptDao.getQuizAttempts(quizAttempt.getQuizId()));

        //Testing getQuizAttempts If score is sorted descending
        List<QuizAttempt> quizAttempts2 = new ArrayList<>();
        quizAttempts2.add(quizAttempt2);
        quizAttempts2.add(quizAttempt3);
        assertEquals(quizAttempts2, quizAttemptDao.getQuizAttempts(quizAttempt2.getQuizId()));

        //Testing getQuizAttempts, when same score, should be sorted with time passed since start of a quiz
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt2.getAttemptId(), 200));
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt3.getAttemptId(), 200));
        QuizAttempt freshAttempt2 = quizAttemptDao.getAttemptById(quizAttempt2.getAttemptId());
        QuizAttempt freshAttempt3 = quizAttemptDao.getAttemptById(quizAttempt3.getAttemptId());
        List<QuizAttempt> quizAttempts3 = new ArrayList<>();
        quizAttempts3.add(freshAttempt3);
        quizAttempts3.add(freshAttempt2);
        assertEquals(quizAttempts3, quizAttemptDao.getQuizAttempts(quizAttempt2.getQuizId()));
    }

    //Testing getTodaysTopPerformers
    @Test
    public void testTodaysTopPerformers() throws InterruptedException, SQLException {
        //Creating "and old attempt"
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO UserQuizAttempts (user_id, quiz_id, start_time, end_time, score, time_taken_seconds) " +
                        "VALUES (?, ?, '2023-01-01 10:00:00', '2023-01-01 11:00:00', 300.0, 3600)");
        stmt.setInt(1, userId);
        stmt.setInt(2, quizAttempt.getQuizId());
        stmt.executeUpdate();

        assertTrue(quizAttemptDao.createAttempt(quizAttempt));
        Thread.sleep(1000);
        assertTrue(quizAttemptDao.createAttempt(quizAttempt2));
        Thread.sleep(2000);
        QuizAttempt quizAttempt3 = new QuizAttempt(userId, quizAttempt.getQuizId());
        assertTrue(quizAttemptDao.createAttempt(quizAttempt3));

        //Creating attempts
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt.getAttemptId(), 50));
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt2.getAttemptId(), 150));
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt3.getAttemptId(), 100));

        //Testing getTodaysTopPerformers
        List<QuizAttempt> todaysTop = quizAttemptDao.getTodaysTopPerformers(quizAttempt.getQuizId());
        assertEquals(2, todaysTop.size());
        assertEquals(100, todaysTop.get(0).getScore());
        assertEquals(50, todaysTop.get(1).getScore());

        //Testing if sorted by start time when Scores are the same
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt.getAttemptId(), 200));
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt3.getAttemptId(), 200));
        List<QuizAttempt> todaysTop2 = quizAttemptDao.getTodaysTopPerformers(quizAttempt.getQuizId());
        assertEquals(quizAttempt3.getAttemptId(), todaysTop2.get(0).getAttemptId());
        assertEquals(quizAttempt.getAttemptId(), todaysTop2.get(1).getAttemptId());

        stmt.close();
    }

    //Testing getRecentPerformers
    @Test
    public void testGetRecentPerformers() throws InterruptedException{
        assertTrue(quizAttemptDao.createAttempt(quizAttempt));
        Thread.sleep(1000);
        assertTrue(quizAttemptDao.createAttempt(quizAttempt2));
        Thread.sleep(1000);
        QuizAttempt quizAttempt3 = new QuizAttempt(userId, quizAttempt.getQuizId());
        assertTrue(quizAttemptDao.createAttempt(quizAttempt3));

        //Creating attempts
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt.getAttemptId(), 70));
        Thread.sleep(1000);
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt2.getAttemptId(), 80));
        Thread.sleep(1000);
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt3.getAttemptId(), 90));

        //Testing getRecentPerformers ordered by completion time
        List<QuizAttempt> recent = quizAttemptDao.getRecentPerformers(quizAttempt.getQuizId(), 2);
        assertEquals(2, recent.size());
        assertEquals(quizAttempt3.getAttemptId(), recent.get(0).getAttemptId());
        assertEquals(quizAttempt.getAttemptId(), recent.get(1).getAttemptId());

        //Testing limit
        List<QuizAttempt> recent2 = quizAttemptDao.getRecentPerformers(quizAttempt.getQuizId(), 1);
        assertEquals(1, recent2.size());
        assertEquals(quizAttempt3.getAttemptId(), recent2.get(0).getAttemptId());
    }

    //Testing getUserHistory
    @Test
    public void testGetUserQuizHistory() throws InterruptedException{
        assertTrue(quizAttemptDao.createAttempt(quizAttempt));
        Thread.sleep(1000);
        QuizAttempt quizAttempt3 = new QuizAttempt(userId, quizAttempt.getQuizId());
        assertTrue(quizAttemptDao.createAttempt(quizAttempt3));

        //adding attempts
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt.getAttemptId(), 60));
        assertTrue(quizAttemptDao.completeAttempt(quizAttempt3.getAttemptId(), 75));

        //Tests the method and sorting
        List<QuizAttempt> userHistory = quizAttemptDao.getUserQuizHistory(userId, quizAttempt.getQuizId());
        assertEquals(2, userHistory.size());
        assertEquals(quizAttempt3.getAttemptId(), userHistory.get(0).getAttemptId());
        assertEquals(quizAttempt.getAttemptId(), userHistory.get(1).getAttemptId());
    }

    //Test invalid quiz attempt state while inserting
    @Test
    public void testIllegalAttemptState(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> quizAttemptDao.createAttempt(new QuizAttempt(Integer.MAX_VALUE, userId, quizAttempt.getQuizId(),
                        Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()), 10))
        );
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> quizAttemptDao.createAttempt(new QuizAttempt(Integer.MAX_VALUE, userId, quizAttempt.getQuizId(),
                        Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()), 0))
        );
        IllegalArgumentException exception3 = assertThrows(
                IllegalArgumentException.class,
                () -> quizAttemptDao.createAttempt(new QuizAttempt(userId, quizAttempt.getQuizId(),
                        Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()), 0))
        );
        IllegalArgumentException exception4 = assertThrows(
                IllegalArgumentException.class,
                () -> quizAttemptDao.createAttempt(new QuizAttempt(userId, quizAttempt.getQuizId(),
                        null, Timestamp.valueOf(LocalDateTime.now()), 0))
        );
        assertEquals("attempt id, quiz start time, end time and getScore must be assigned by this class", exception.getMessage());
    }

    //Testing errors
    @Test
    public void testErrors(){
        //Test createQuizAttempt
        QuizAttempt invalidUserAttempt = new QuizAttempt(Integer.MAX_VALUE, quizAttempt.getQuizId());
        assertFalse(quizAttemptDao.createAttempt(invalidUserAttempt));
        QuizAttempt invalidQuizAttempt = new QuizAttempt(userId, Integer.MAX_VALUE);
        assertFalse(quizAttemptDao.createAttempt(invalidQuizAttempt));

        //Testing getAttemptById
        assertNull(quizAttemptDao.getAttemptById(Integer.MAX_VALUE));

        //Testing completeAttempt
        assertFalse(quizAttemptDao.completeAttempt(Integer.MAX_VALUE, 50));

        //Testing getUserAttempts
        List<QuizAttempt> emptyList = quizAttemptDao.getUserAttempts(Integer.MAX_VALUE);
        assertTrue(emptyList.isEmpty());

        //Testing getQuizAttempts
        List<QuizAttempt> emptyQuizList = quizAttemptDao.getQuizAttempts(Integer.MAX_VALUE);
        assertTrue(emptyQuizList.isEmpty());

        //Testing getTodaysTopPerformers
        List<QuizAttempt> quizAttempts = quizAttemptDao.getTodaysTopPerformers(Integer.MAX_VALUE);

        //Testing getRecentPerformers
        List<QuizAttempt> emptyRecentList = quizAttemptDao.getRecentPerformers(Integer.MAX_VALUE, 5);
        assertTrue(emptyRecentList.isEmpty());

        //Testing getUserQuizHistory
        List<QuizAttempt> emptyHistoryList = quizAttemptDao.getUserQuizHistory(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertTrue(emptyHistoryList.isEmpty());
    }
}
