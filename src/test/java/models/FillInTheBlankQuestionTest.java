package models;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;

public class FillInTheBlankQuestionTest {
    FillInTheBlankQuestion fillInTheBlankQuestion;

    //Sets up the question that will be tested
    @BeforeEach
    public void setUp() {
        int questionId = 1;
        String questionText = "The capital of Georgia is _____ and it was founded by _____.";
        int quizId = 1;
        ArrayList<HashSet<String>> answers = generateCorrectAnswers();
        fillInTheBlankQuestion = new FillInTheBlankQuestion(questionId, questionText, answers, quizId, 0, 2);
    }

    //Tests all the getters and setters of the question
    @Test
    public void testGettersAndSetters() {
        assertNull(fillInTheBlankQuestion.getImageUrl());
        assertEquals(0, fillInTheBlankQuestion.getOrderInQuiz());
        String imageUrl = "www.link.com";
        fillInTheBlankQuestion.setImageUrl(imageUrl);
        assertEquals(imageUrl, fillInTheBlankQuestion.getImageUrl());
        fillInTheBlankQuestion.setOrderInQuiz(9);
        assertEquals(9, fillInTheBlankQuestion.getOrderInQuiz());
        assertEquals("FILL_IN_BLANK", fillInTheBlankQuestion.getQuestionType());
        assertEquals("The capital of Georgia is _____ and it was founded by _____.", fillInTheBlankQuestion.getQuestionText());
        assertEquals(1, fillInTheBlankQuestion.getQuestionId());
        assertEquals(1, fillInTheBlankQuestion.getQuizId());
        ArrayList<HashSet<String>> answers = new ArrayList<>();
        answers.add(new HashSet<>());
        answers.add(new HashSet<>());
        answers.get(0).add("Tbilisi");
        answers.get(1).add("Vakhtang Gorgasali");
        answers.get(1).add("Vakhtang I Gorgasali");
        answers.get(1).add("Gorgasali");
        assertEquals(answers, fillInTheBlankQuestion.getCorrectAnswers());
        assertNotSame(answers, fillInTheBlankQuestion.getCorrectAnswers());
    }

    //Checks checkUserAnswers() function when all the provided answers were true
    @Test
    public void testCheckUserAnswersTrue() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Tbilisi");
        answers.add("Vakhtang Gorgasali");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(true);
        correctAnswers.add(true);
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
        answers.set(1, "Gorgasali");
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
        answers.set(1, "Vakhtang I Gorgasali");
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
    }

    //Checks checkUserAnswers() function when all the provided answers were false
    @Test
    public void testCheckUserAnswersFalse() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Tbilis");
        answers.add("Vakhtang");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(false);
        correctAnswers.add(false);
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
        answers.set(0, "tbilisi");
        answers.set(1, "vakhtang Gorgasali");
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
    }

    /*
     * Checks checkUserAnswers() function when some of the provided answers were false
     * and some of them where true
     */
    @Test
    public void testCheckUserAnswersSomeTrue() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Tbilisi");
        answers.add("Vakhtang gorgasali");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(true);
        correctAnswers.add(false);
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
        answers.set(0, "tbilisi");
        answers.set(1, "Vakhtang Gorgasali");
        correctAnswers.set(0, false);
        correctAnswers.set(1, true);
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
    }

    //Checks if the blank answers/Recurring Spaces are accepted as answers
    @Test
    public void testCheckUserAnswersBlank() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("");
        answers.add("    ");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(false);
        correctAnswers.add(false);
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
    }

    //Checks if the submitted answers are trimmed correctly
    @Test
    public void testCheckUserAnswersTrailingSpaces() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("   Tbilisi ");
        answers.add(" Vakhtang  Gorgasali    ");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(true);
        correctAnswers.add(true);
        assertEquals(correctAnswers, fillInTheBlankQuestion.checkAnswers(answers));
    }

    //Tests if the exception is thrown when incorrect N of answers are provided
    @Test
    public void testUserAnswerExceptions(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Tbilisi");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fillInTheBlankQuestion.checkAnswers(answers)
        );
        assertEquals("Wrong number of user answers", exception.getMessage());
        answers.add("Tbilisi");
        answers.add("Tbilisi");
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> fillInTheBlankQuestion.checkAnswers(answers)
        );
        assertEquals("Wrong number of user answers", exception2.getMessage());
    }

    //Tests if score calculation is correct for any Number of correct answers
    @Test
    public void testCheckCalculateScore(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Tbilisi");
        answers.add("Vakhtang gorgasali");
        assertEquals(1, fillInTheBlankQuestion.calculateScore(answers), 0.001);
        answers.set(0, "tbilisi");
        assertEquals(0, fillInTheBlankQuestion.calculateScore(answers));
        answers.set(1, "Vakhtang Gorgasali");
        assertEquals(1, fillInTheBlankQuestion.calculateScore(answers), 0.001);
        answers.set(0, "Tbilisi");
        assertEquals(2, fillInTheBlankQuestion.calculateScore(answers));
    }

    //Checks the overridden equals() function
    @Test
    public void testEquals(){
        FillInTheBlankQuestion fillInTheBlankQuestionCopy = fillInTheBlankQuestion;
        assertEquals(fillInTheBlankQuestionCopy, fillInTheBlankQuestion);
        ArrayList<Integer> randomCollection = new ArrayList<>();
        assertNotEquals(fillInTheBlankQuestion, randomCollection);
        fillInTheBlankQuestion.setImageUrl("imageUrl");
        FillInTheBlankQuestion newQuestion = new FillInTheBlankQuestion(10, "Hi", new ArrayList<>(), 10, 1, 1);
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        newQuestion = new FillInTheBlankQuestion(10, "Hi", new ArrayList<>(), 1, 1, 1);
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        newQuestion = new FillInTheBlankQuestion(10, "The capital of Georgia is _____ and it was founded by _____.", new ArrayList<>(), 1, 1, 1);
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        ArrayList<HashSet<String>> answers = generateCorrectAnswers();
        newQuestion = new FillInTheBlankQuestion(10, "The capital of Georgia is _____ and it was founded by _____.", answers, 1, 1, 1);
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        newQuestion = new FillInTheBlankQuestion(1, "The capital of Georgia is _____ and it was founded by _____.", answers, 1, 1, 1);
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        newQuestion = new FillInTheBlankQuestion(1, "The capital of Georgia is _____ and it was founded by _____.", answers, 1, 0, 1);
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        newQuestion = new FillInTheBlankQuestion(1, "The capital of Georgia is _____ and it was founded by _____.", answers, 1, 0, 2);
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        newQuestion.setImageUrl("imageUrl");
        assertEquals(fillInTheBlankQuestion, newQuestion);
    }

    /*
     * Tests if changing the id to a constructor without an id gets us the same
     * object as a normal constructor
     */
    @Test
    public void testConstructorWithoutId(){
        ArrayList<HashSet<String>> answers = generateCorrectAnswers();
        Question newQuestion = new FillInTheBlankQuestion("The capital of Georgia is _____ and it was founded by _____.", answers, 1, 0, 1);
        assertEquals(-1, newQuestion.getQuestionId());
        assertNotEquals(fillInTheBlankQuestion, newQuestion);
        newQuestion.setQuestionId(1);
        assertEquals(1, newQuestion.getQuestionId());
        assertEquals(fillInTheBlankQuestion, newQuestion);
    }

    //tests setQuestionId
    @Test
    public void testSetQuestionId(){
        ArrayList<HashSet<String>> answers = generateCorrectAnswers();
        FillInTheBlankQuestion question = new FillInTheBlankQuestion("Hi", answers, 1, 0, 1);
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
    private ArrayList<HashSet<String>> generateCorrectAnswers(){
        ArrayList<HashSet<String>> answers = new ArrayList<>();
        answers.add(new HashSet<>());
        answers.add(new HashSet<>());
        answers.get(0).add("Tbilisi");
        answers.get(1).add("Vakhtang Gorgasali");
        answers.get(1).add("Vakhtang I Gorgasali");
        answers.get(1).add("Gorgasali");
        return answers;
    }
}
