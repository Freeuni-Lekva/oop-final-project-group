package models;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class MultipleChoiceQuestionTest {
    MultipleChoiceQuestion multipleChoiceQuestion;

    //Sets up the question that will be tested
    @BeforeEach
    public void setUp() {
        String questionText = "What is the capital of France?";
        int quizId = 1;
        int questionId = 1;
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Paris", true);
        answers.put("London", false);
        answers.put("Berlin", false);
        answers.put("Madrid", false);
        multipleChoiceQuestion = new MultipleChoiceQuestion(questionId, questionText, answers, quizId, 0, 2);
    }

    //Tests all the getters and setters of the question
    @Test
    public void testGettersAndSetters() {
        assertNull(multipleChoiceQuestion.getImageUrl());
        assertEquals(0, multipleChoiceQuestion.getOrderInQuiz());
        String imageUrl = "www.link.com";
        multipleChoiceQuestion.setImageUrl(imageUrl);
        assertEquals(imageUrl, multipleChoiceQuestion.getImageUrl());
        multipleChoiceQuestion.setOrderInQuiz(9);
        assertEquals(9, multipleChoiceQuestion.getOrderInQuiz());
        assertEquals("MULTIPLE_CHOICE", multipleChoiceQuestion.getQuestionType());
        assertEquals("What is the capital of France?", multipleChoiceQuestion.getQuestionText());
        assertEquals(1, multipleChoiceQuestion.getQuestionId());
        assertEquals(1, multipleChoiceQuestion.getQuizId());
        assertEquals(2, multipleChoiceQuestion.getMaxScore());
        Map<String, Boolean> answers = generateCorrectAnswers();
        assertEquals(answers, multipleChoiceQuestion.getOptions());
        assertNotSame(answers, multipleChoiceQuestion.getOptions());
    }

    //Checks checkUserAnswers() function when the provided answer is correct
    @Test
    public void testCheckUserAnswersCorrect() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Paris");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(true);
        assertEquals(correctAnswers, multipleChoiceQuestion.checkAnswers(answers));
    }

    //Checks checkUserAnswers() function when the provided answer is incorrect
    @Test
    public void testCheckUserAnswersIncorrect() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("London");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(false);
        assertEquals(correctAnswers, multipleChoiceQuestion.checkAnswers(answers));
    }

    /*
     * Checks if the exception is thrown when the user provided wrong number of answers
     * or when they provided answers that don't exist
     */
    @Test
    public void testUserAnswerExceptions(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("paris");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion.checkAnswers(answers)
        );
        assertEquals("Invalid answer option!", exception.getMessage());

        answers.clear();
        answers.add("Paris");
        answers.add("London");
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion.checkAnswers(answers)
        );
        assertEquals("Must select exactly one answer!", exception2.getMessage());

        answers.clear();
        IllegalArgumentException exception3 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion.checkAnswers(answers)
        );
        assertEquals("Must select exactly one answer!", exception3.getMessage());
    }

    /*
     * Checks if an error is thrown when in constructor options don't have exactly one correct answer
     */
    @Test
    public void testCheckOptionsExceptions(){
        int questionId = 1;
        String questionText = "What is the capital of France?";
        int quizId = 1;
        int orderInQuiz = 0;
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Paris", false);
        answers.put("London", false);
        double maxScore = 2;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new MultipleChoiceQuestion(questionId, questionText, answers, quizId, orderInQuiz, maxScore)
        );
        assertEquals("Multiple choice questions must have exactly one correct answer", exception.getMessage());

        answers.put("Paris", true);
        answers.put("London", true);
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> new MultipleChoiceQuestion(questionId, questionText, answers, quizId, orderInQuiz, maxScore)
        );
        assertEquals("Multiple choice questions must have exactly one correct answer", exception2.getMessage());
    }

    //Tests if score calculation is correct
    @Test
    public void testCheckCalculateScore(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Paris");
        assertEquals(2.0, multipleChoiceQuestion.calculateScore(answers));
        answers.set(0, "London");
        assertEquals(0.0, multipleChoiceQuestion.calculateScore(answers));
    }

    //Tests getCorrectAnswers method
    @Test
    public void testGetCorrectAnswers(){
        List<String> correctAnswers = multipleChoiceQuestion.getCorrectAnswers();
        assertEquals(1, correctAnswers.size());
        assertTrue(correctAnswers.contains("Paris"));
    }

    //Checks the overridden equals() function
    @Test
    public void testEquals(){
        multipleChoiceQuestion.setImageUrl("www.link.com");
        MultipleChoiceQuestion multipleChoiceQuestionCopy = multipleChoiceQuestion;
        assertEquals(multipleChoiceQuestionCopy, multipleChoiceQuestion);
        ArrayList<Integer> randomCollection = new ArrayList<>();
        assertNotEquals(multipleChoiceQuestion, randomCollection);
        assertNotEquals(multipleChoiceQuestion, null);

        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Berlin", true);
        answers.put("Paris", false);
        MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(10, "Hi", answers, 10, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(10, "Hi", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(10, "What is the capital of France?", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        answers = generateCorrectAnswers();
        newQuestion = new MultipleChoiceQuestion(10, "What is the capital of France?", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(1, "What is the capital of France?", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(1, "What is the capital of France?", answers, 1, 0, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(1, "What is the capital of France?", answers, 1, 0, 2);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion.setImageUrl("www.link.com");
        assertEquals(multipleChoiceQuestion, newQuestion);
    }

    /*
     * Tests if changing the id to a constructor without an id gets us the same
     * object as a normal constructor
     */
    @Test
    public void testConstructorWithoutId(){
        Map<String, Boolean> answers = generateCorrectAnswers();
        Question newQuestion = new MultipleChoiceQuestion("What is the capital of France?", answers, 1, 0, 2);
        assertEquals(-1, newQuestion.getQuestionId());
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion.setQuestionId(1);
        assertEquals(multipleChoiceQuestion, newQuestion);
    }

    //tests setQuestionId
    @Test
    public void testSetQuestionId(){
        Map<String, Boolean> answers = generateCorrectAnswers();
        MultipleChoiceQuestion question = new MultipleChoiceQuestion("Hi", answers, 1, 0, 1);
        assertEquals(-1, question.getQuestionId());
        question.setQuestionId(2);
        assertEquals(2, question.getQuestionId());
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> question.setQuestionId(3)
        );
        assertEquals("Question id can only been assigned once!", exception.getMessage());
    }

    //Private function for generating the correct answers for a question used in tests
    private Map<String, Boolean> generateCorrectAnswers() {
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Paris", true);
        answers.put("London", false);
        answers.put("Berlin", false);
        answers.put("Madrid", false);
        return answers;
    }
}
