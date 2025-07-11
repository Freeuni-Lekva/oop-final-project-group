package models;

import org.junit.jupiter.api.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class QuizAttemptTest {
    private Timestamp startTime;
    private Timestamp endTime;

    //Set up of a QuizAttempt tested
    @BeforeEach
    public void setUp() {
        startTime = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        endTime = Timestamp.valueOf(LocalDateTime.now());
    }

    //Tests the default constructor, getters and setAttemptId
    @Test
    public void testDefaultConstructor(){
        QuizAttempt quizAttempt = new QuizAttempt(1, 2);
        assertEquals(1, quizAttempt.getUserId());
        assertEquals(2, quizAttempt.getQuizId());
        assertNull(quizAttempt.getStartTime());
        assertNull(quizAttempt.getEndTime());
        assertEquals(0, quizAttempt.getScore());
        assertEquals(0, quizAttempt.getTimeTakenSeconds());
        assertEquals(-1, quizAttempt.getAttemptId());
        quizAttempt.setAttemptId(5);
        assertEquals(5, quizAttempt.getAttemptId());
    }

    //Tests constructor without id, getters and setAttemptId
    @Test
    public void testConstructorWithoutId() {
        QuizAttempt quizAttempt = new QuizAttempt(1, 2, startTime, endTime, 50);
        assertEquals(1, quizAttempt.getUserId());
        assertEquals(2, quizAttempt.getQuizId());
        assertEquals(startTime, quizAttempt.getStartTime());
        assertEquals(endTime, quizAttempt.getEndTime());
        assertEquals(50, quizAttempt.getScore());
        assertEquals(24 * 60 * 60, quizAttempt.getTimeTakenSeconds());
        assertEquals(-1, quizAttempt.getAttemptId());
        quizAttempt.setAttemptId(5);
        assertEquals(5, quizAttempt.getAttemptId());
    }

    //Tests constructor with id, getters and setAttemptId
    @Test
    public void testConstructorWithId() {
        Timestamp currStartTime = Timestamp.valueOf(LocalDateTime.now().minusDays(5));
        Timestamp currEndTime = Timestamp.valueOf(LocalDateTime.now().plusHours(2));
        QuizAttempt quizAttempt = new QuizAttempt(5, 10, 15, currStartTime, currEndTime, 100);
        assertEquals(5, quizAttempt.getAttemptId());
        assertEquals(10, quizAttempt.getUserId());
        assertEquals(15, quizAttempt.getQuizId());
        assertEquals(currStartTime, quizAttempt.getStartTime());
        assertEquals(currEndTime, quizAttempt.getEndTime());
        assertEquals(100, quizAttempt.getScore());
        assertEquals((5 * 24 + 2) * 60 * 60, quizAttempt.getTimeTakenSeconds());
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> quizAttempt.setAttemptId(5)
        );
        assertEquals("Attempt id can only be assigned once!", exception.getMessage());
    }

    //Tests toString method
    @Test
    public void testToString() {
        QuizAttempt quizAttempt = new QuizAttempt(1, 2, startTime, endTime, 50);
        assertEquals(quizAttempt.getAttemptId() + " " + quizAttempt.getUserId() +
                " " + quizAttempt.getQuizId() + " " + quizAttempt.getStartTime() + " " +
                quizAttempt.getEndTime() + " " + quizAttempt.getScore(), quizAttempt.toString());
    }

    //Tests equals method
    @Test
    public void testEquals() {
        QuizAttempt quizAttempt = new QuizAttempt(1, 2, startTime, endTime, 50);
        assertEquals(quizAttempt, quizAttempt);
        ArrayList<QuizAttempt> quizAttempts = new ArrayList<>();
        assertNotEquals(quizAttempt, quizAttempts);
        QuizAttempt testAttempt = new QuizAttempt(5, 2, 1,
                Timestamp.valueOf(LocalDateTime.now().minusDays(5)), Timestamp.valueOf(LocalDateTime.now().plusHours(2)), 1000);
        assertNotEquals(quizAttempt, testAttempt);
        testAttempt = new QuizAttempt(2, 1, Timestamp.valueOf(LocalDateTime.now().minusDays(5)),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)), 1000);
        assertNotEquals(quizAttempt, testAttempt);
        testAttempt = new QuizAttempt(2, 1, startTime,
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)), 1000);
        assertNotEquals(quizAttempt, testAttempt);
        testAttempt = new QuizAttempt(2, 1, startTime, endTime, 1000);
        assertNotEquals(quizAttempt, testAttempt);
        testAttempt = new QuizAttempt(2, 1, startTime, endTime, 50);
        assertNotEquals(quizAttempt, testAttempt);
        testAttempt = new QuizAttempt(1, 1, startTime, endTime, 50);
        assertNotEquals(quizAttempt, testAttempt);
        testAttempt = new QuizAttempt(1, 2, startTime, endTime, 50);
        assertEquals(quizAttempt, testAttempt);
        quizAttempt.setAttemptId(5);
        assertNotEquals(quizAttempt, testAttempt);
    }
}
