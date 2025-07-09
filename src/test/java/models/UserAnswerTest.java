package models;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class UserAnswerTest {
    private UserAnswer userAnswerWithId;
    private UserAnswer userAnswerWithoutId;

    //Sets up userAnswer for future tests
    @BeforeEach
    public void setUp() {
        userAnswerWithoutId = new UserAnswer(5, 10, "Answer 1", null, true);
        userAnswerWithId = new UserAnswer(1,2, 11, "Answer 2", 2, false);
    }

    //Tests constructor without id, with getters and an only setter.
    @Test
    public void testConstructorWithoutId(){
        assertEquals(5, userAnswerWithoutId.getAttemptId());
        assertEquals(10, userAnswerWithoutId.getQuestionId());
        assertEquals("Answer 1", userAnswerWithoutId.getAnswerGivenText());
        assertNull(userAnswerWithoutId.getSelectedOptionId());
        assertTrue(userAnswerWithoutId.isCorrect());
        assertEquals(-1, userAnswerWithoutId.getUserAnswerId());
        userAnswerWithoutId.setUserAnswerId(5);
        assertEquals(5, userAnswerWithoutId.getUserAnswerId());
    }

    //Tests constructor with id, with getters and an only setter
    @Test
    public void testConstructorWithId(){
        assertEquals(1, userAnswerWithId.getUserAnswerId());
        assertEquals(2, userAnswerWithId.getAttemptId());
        assertEquals(11, userAnswerWithId.getQuestionId());
        assertEquals("Answer 2", userAnswerWithId.getAnswerGivenText());
        assertEquals(2, userAnswerWithId.getSelectedOptionId());
        assertFalse(userAnswerWithId.isCorrect());
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userAnswerWithId.setUserAnswerId(5)
        );
        assertEquals("User Answer Id can only be assigned once!", exception.getMessage());
    }

    //Tests toString method
    @Test
    public void testToString() {
        assertEquals(userAnswerWithoutId.getUserAnswerId() + " " + userAnswerWithoutId.getAttemptId() + " " +
                userAnswerWithoutId.getQuestionId() + " " + userAnswerWithoutId.getAnswerGivenText() + " " +
                userAnswerWithoutId.getSelectedOptionId() + " " + userAnswerWithoutId.isCorrect(),
                userAnswerWithoutId.toString());
    }

    //Tests equals method
    @Test
    public void testEquals() {
        assertEquals(userAnswerWithoutId, userAnswerWithoutId);
        ArrayList<UserAnswer> userAnswers = new ArrayList<>();
        assertNotEquals(userAnswerWithoutId, userAnswers);
        UserAnswer userAnswer = new UserAnswer(5, 10, 5, "Answer 3", 5, false);
        assertNotEquals(userAnswerWithoutId, userAnswer);
        userAnswer = new UserAnswer(10, 5, "Answer 3", 5, false);
        assertNotEquals(userAnswerWithoutId, userAnswer);
        userAnswer = new UserAnswer(5, 5, "Answer 3", 5, false);
        assertNotEquals(userAnswerWithoutId, userAnswer);
        userAnswer = new UserAnswer(5, 10, "Answer 3", 5, false);
        assertNotEquals(userAnswerWithoutId, userAnswer);
        userAnswer = new UserAnswer(5, 10, "Answer 1", 5, false);
        assertNotEquals(userAnswerWithoutId, userAnswer);
        userAnswer = new UserAnswer(5, 10, "Answer 1", null, false);
        assertNotEquals(userAnswerWithoutId, userAnswer);
        userAnswer = new UserAnswer(5, 10, "Answer 1", null, true);
        assertEquals(userAnswerWithoutId, userAnswer);
        userAnswer.setUserAnswerId(5);
        assertNotEquals(userAnswerWithoutId, userAnswer);
    }
}
