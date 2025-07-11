package models;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class MultipleChoiceWithMultipleAnswersQuestionTest {
    MultipleChoiceWithMultipleAnswersQuestion multipleChoiceWithMultipleAnswersQuestion;

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
        multipleChoiceWithMultipleAnswersQuestion = new MultipleChoiceWithMultipleAnswersQuestion(questionId, questionText, answers, quizId, 0, 2);
    }

    //Tests all the getters and setters of the question
    @Test
    public void testGettersAndSetters() {
        assertNull(multipleChoiceWithMultipleAnswersQuestion.getImageUrl());
        assertEquals(0, multipleChoiceWithMultipleAnswersQuestion.getOrderInQuiz());
        String imageUrl = "www.link.com";
        multipleChoiceWithMultipleAnswersQuestion.setImageUrl(imageUrl);
        assertEquals(imageUrl, multipleChoiceWithMultipleAnswersQuestion.getImageUrl());
        multipleChoiceWithMultipleAnswersQuestion.setOrderInQuiz(9);
        assertEquals(9, multipleChoiceWithMultipleAnswersQuestion.getOrderInQuiz());
        assertEquals("MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS", multipleChoiceWithMultipleAnswersQuestion.getQuestionType());
        assertEquals("Which of these countries are located in Europe?", multipleChoiceWithMultipleAnswersQuestion.getQuestionText());
        assertEquals(1, multipleChoiceWithMultipleAnswersQuestion.getQuestionId());
        assertEquals(1, multipleChoiceWithMultipleAnswersQuestion.getQuizId());
        assertEquals(2, multipleChoiceWithMultipleAnswersQuestion.getMaxScore());
        Map<String, Boolean> answers = generateCorrectAnswers();
        assertEquals(answers, multipleChoiceWithMultipleAnswersQuestion.getOptions());
        assertNotSame(answers, multipleChoiceWithMultipleAnswersQuestion.getOptions());
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
        assertEquals(correctAnswers, multipleChoiceWithMultipleAnswersQuestion.checkAnswers(answers));
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
        assertEquals(correctAnswers, multipleChoiceWithMultipleAnswersQuestion.checkAnswers(answers));
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
        assertEquals(correctAnswers, multipleChoiceWithMultipleAnswersQuestion.checkAnswers(answers));
        answers.add("Brazil");
        answers.add("Spain");
        answers.add("France");
        correctAnswers.add(false);
        correctAnswers.add(true);
        correctAnswers.add(true);
        assertEquals(correctAnswers, multipleChoiceWithMultipleAnswersQuestion.checkAnswers(answers));
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
                () -> multipleChoiceWithMultipleAnswersQuestion.checkAnswers(answers)
        );
        assertEquals("Wrong answer at index 0!", exception.getMessage());
        answers.set(0, "Germany");
        answers.add("japan");
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceWithMultipleAnswersQuestion.checkAnswers(answers)
        );
        assertEquals("Wrong answer at index 1!", exception2.getMessage());
        answers.set(1, "Japan");
        answers.add("Japan");
        answers.add("Japan");
        answers.add("Japan");
        answers.add("Japan");
        IllegalArgumentException exception3 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceWithMultipleAnswersQuestion.checkAnswers(answers)
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
        double maxScore = 2;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceWithMultipleAnswersQuestion = new MultipleChoiceWithMultipleAnswersQuestion(questionId, questionText, answers, quizId, orderInQuiz, maxScore)
        );
        assertEquals("At least one option must be correct", exception.getMessage());
        answers.remove("Brazil");
        answers.remove("Japan");
        answers.put("Spain", true);
        answers.put("France", true);
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> multipleChoiceWithMultipleAnswersQuestion = new MultipleChoiceWithMultipleAnswersQuestion(questionId, questionText, answers, quizId, orderInQuiz, maxScore)
        );
        assertEquals("All options can't be correct", exception2.getMessage());
    }

    //Tests if score calculation is correct for any Number of correct answers
    @Test
    public void testCheckCalculateScore(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add("France");
        assertEquals(0.3333 * 2, multipleChoiceWithMultipleAnswersQuestion.calculateScore(answers), 0.001);
        answers.add("Spain");
        assertEquals(0.6666 * 2, multipleChoiceWithMultipleAnswersQuestion.calculateScore(answers),0.001);
        answers.add("Germany");
        assertEquals(2, multipleChoiceWithMultipleAnswersQuestion.calculateScore(answers));
        answers.add("Japan");
        assertEquals(0, multipleChoiceWithMultipleAnswersQuestion.calculateScore(answers));
        answers.set(3, "Brazil");
        assertEquals(0, multipleChoiceWithMultipleAnswersQuestion.calculateScore(answers));
    }

    //Checks the overridden equals() function
    @Test
    public void testEquals(){
        multipleChoiceWithMultipleAnswersQuestion.setImageUrl("www.link.com");
        MultipleChoiceWithMultipleAnswersQuestion multipleChoiceWithMultipleAnswersQuestionCopy = multipleChoiceWithMultipleAnswersQuestion;
        assertEquals(multipleChoiceWithMultipleAnswersQuestionCopy, multipleChoiceWithMultipleAnswersQuestion);
        ArrayList<Integer> randomCollection = new ArrayList<>();
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, randomCollection);
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Brazil", false);
        answers.put("France", true);
        MultipleChoiceWithMultipleAnswersQuestion newQuestion = new MultipleChoiceWithMultipleAnswersQuestion(10, "Hi", answers, 10, 1, 1);
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        newQuestion = new MultipleChoiceWithMultipleAnswersQuestion(10, "Hi", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        newQuestion = new MultipleChoiceWithMultipleAnswersQuestion(10, "Which of these countries are located in Europe?", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        answers = generateCorrectAnswers();
        newQuestion = new MultipleChoiceWithMultipleAnswersQuestion(10, "Which of these countries are located in Europe?", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        newQuestion = new MultipleChoiceWithMultipleAnswersQuestion(1, "Which of these countries are located in Europe?", answers, 1, 1, 1);
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        newQuestion = new MultipleChoiceWithMultipleAnswersQuestion(1, "Which of these countries are located in Europe?", answers, 1, 0, 1);
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        newQuestion = new MultipleChoiceWithMultipleAnswersQuestion(1, "Which of these countries are located in Europe?", answers, 1, 0, 2);
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        newQuestion.setImageUrl("www.link.com");
        assertEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
    }

    /*
     * Tests if changing the id to a constructor without an id gets us the same
     * object as a normal constructor
     */
    @Test
    public void testConstructorWithoutId(){
        Map<String, Boolean> answers = generateCorrectAnswers();
        Question newQuestion = new MultipleChoiceWithMultipleAnswersQuestion("Which of these countries are located in Europe?", answers, 1, 0, 1);
        assertEquals(-1, newQuestion.getQuestionId());
        assertNotEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
        newQuestion.setQuestionId(1);
        assertEquals(multipleChoiceWithMultipleAnswersQuestion, newQuestion);
    }

    //tests setQuestionId
    @Test
    public void testSetQuestionId(){
        Map<String, Boolean> answers = generateCorrectAnswers();
        MultipleChoiceWithMultipleAnswersQuestion question = new MultipleChoiceWithMultipleAnswersQuestion("Hi", answers, 1, 0, 1);
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