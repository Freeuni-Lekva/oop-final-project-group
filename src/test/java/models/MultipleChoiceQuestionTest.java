package models;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class MultipleChoiceQuestionTest {
    MultipleChoiceQuestion multipleChoiceQuestion;

    //Sets up the question that will be tested
    @BeforeEach
    public void setUp() {
        String questionText = "Which of these countries are located in Europe?";
        int quizId = 1;
        int questionId = 1;
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Germany", true);
        answers.put("Brazil", false);
        answers.put("France", true);
        answers.put("Japan", false);
        answers.put("Spain", true);
        multipleChoiceQuestion = new MultipleChoiceQuestion(questionId, questionText, answers, quizId, 0);
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
        assertEquals("Which of these countries are located in Europe?", multipleChoiceQuestion.getQuestionText());
        assertEquals(1, multipleChoiceQuestion.getQuestionId());
        assertEquals(1, multipleChoiceQuestion.getQuizId());
        Map<String, Boolean> answers = generateCorrectAnswers();
        assertEquals(answers, multipleChoiceQuestion.getOptions());
        assertNotSame(answers, multipleChoiceQuestion.getOptions());
    }

    //Checks checkUserAnswers() function when all the provided answers were true
    @Test
    public void testCheckUserAnswersTrue() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Germany");
        answers.add("France");
        answers.add("Spain");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(true);
        correctAnswers.add(true);
        correctAnswers.add(true);
        assertEquals(correctAnswers, multipleChoiceQuestion.checkAnswers(answers));
    }

    //Checks checkUserAnswers() function when all the provided answers were false
    @Test
    public void testCheckUserAnswersFalse() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Brazil");
        answers.add("Japan");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(false);
        correctAnswers.add(false);
        assertEquals(correctAnswers, multipleChoiceQuestion.checkAnswers(answers));
    }

    /*
     * Checks checkUserAnswers() function when some of the provided answers were false
     * and some of them where true
     */
    @Test
    public void testCheckUserAnswersSomeTrue() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("Germany");
        answers.add("Japan");
        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(true);
        correctAnswers.add(false);
        assertEquals(correctAnswers, multipleChoiceQuestion.checkAnswers(answers));
        answers.add("Brazil");
        answers.add("Spain");
        answers.add("France");
        correctAnswers.add(false);
        correctAnswers.add(true);
        correctAnswers.add(true);
        assertEquals(correctAnswers, multipleChoiceQuestion.checkAnswers(answers));
    }

    /*
     * Checks if the exception is thrown when the user provided too many answers
     * or when they provided answers that don't exist
     */
    @Test
    public void testUserAnswerExceptions(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("germany");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion.checkAnswers(answers)
        );
        assertEquals("Wrong answer at index 0!", exception.getMessage());
        answers.set(0, "Germany");
        answers.add("japan");
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion.checkAnswers(answers)
        );
        assertEquals("Wrong answer at index 1!", exception2.getMessage());
        answers.set(1, "Japan");
        answers.add("Japan");
        answers.add("Japan");
        answers.add("Japan");
        answers.add("Japan");
        IllegalArgumentException exception3 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion.checkAnswers(answers)
        );
        assertEquals("Wrong number of user answers!", exception3.getMessage());
    }

    /*
     * Checks if an error is thrown when in constructor all options where
     * specified correct/incorrect
     */
    @Test
    public void testCheckOptionsExceptions(){
        int questionId = 1;
        String questionText = "Which of these countries are located in Europe?";
        int quizId = 1;
        int orderInQuiz = 0;
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Brazil", false);
        answers.put("Japan", false);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion = new MultipleChoiceQuestion(questionId, questionText, answers, quizId, orderInQuiz)
        );
        assertEquals("At least one option must be correct", exception.getMessage());
        answers.remove("Brazil");
        answers.remove("Japan");
        answers.put("Spain", true);
        answers.put("France", true);
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceQuestion = new MultipleChoiceQuestion(questionId, questionText, answers, quizId, orderInQuiz)
        );
        assertEquals("All options can't be correct", exception2.getMessage());
    }

    //Tests if score calculation is correct for any Number of correct answers
    @Test
    public void testCheckCalculateScore(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("France");
        assertEquals(0.3333, multipleChoiceQuestion.calculateScore(answers), 0.001);
        answers.add("Spain");
        assertEquals(0.6666, multipleChoiceQuestion.calculateScore(answers),0.001);
        answers.add("Germany");
        assertEquals(1, multipleChoiceQuestion.calculateScore(answers));
        answers.add("Japan");
        assertEquals(0, multipleChoiceQuestion.calculateScore(answers));
        answers.set(3, "Brazil");
        assertEquals(0, multipleChoiceQuestion.calculateScore(answers));
    }

    //Checks the overridden equals() function
    @Test
    public void testEquals(){
        multipleChoiceQuestion.setImageUrl("www.link.com");
        MultipleChoiceQuestion multipleChoiceQuestionCopy = multipleChoiceQuestion;
        assertEquals(multipleChoiceQuestionCopy, multipleChoiceQuestion);
        ArrayList<Integer> randomCollection = new ArrayList<>();
        assertNotEquals(multipleChoiceQuestion, randomCollection);
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Brazil", false);
        answers.put("France", true);
        MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(10, "Hi", answers, 10, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(10, "Hi", answers, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(10, "Which of these countries are located in Europe?", answers, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        answers = generateCorrectAnswers();
        newQuestion = new MultipleChoiceQuestion(10, "Which of these countries are located in Europe?", answers, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(1, "Which of these countries are located in Europe?", answers, 1, 1);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion = new MultipleChoiceQuestion(1, "Which of these countries are located in Europe?", answers, 1, 0);
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion.setImageUrl("www.link.com");
    }

    /*
     * Tests if changing the id to a constructor without an id gets us the same
     * object as a normal constructor
     */
    @Test
    public void testConstructorWithoutId(){
        Map<String, Boolean> answers = generateCorrectAnswers();
        Question newQuestion = new MultipleChoiceQuestion("Which of these countries are located in Europe?", answers, 1, 0);
        assertEquals(-1, newQuestion.getQuestionId());
        assertNotEquals(multipleChoiceQuestion, newQuestion);
        newQuestion.setQuestionId(1);
        assertEquals(multipleChoiceQuestion, newQuestion);
    }

    //tests setQuestionId
    @Test
    public void testSetQuestionId(){
        Map<String, Boolean> answers = generateCorrectAnswers();
        MultipleChoiceQuestion question = new MultipleChoiceQuestion("Hi", answers, 1, 0);
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
        answers.put("Germany", true);
        answers.put("Brazil", false);
        answers.put("France", true);
        answers.put("Japan", false);
        answers.put("Spain", true);
        return answers;
    }
}