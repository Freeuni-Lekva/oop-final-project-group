package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QuizTest {
    private Quiz quiz;
    private Quiz quiz2;
    private Quiz quiz3;
    private Quiz quiz4;
    private LocalDateTime dateTime;
    private LocalDateTime dateTime2;

    //Sets up 4 quizzes with 4 different constructors to test each one
    @BeforeEach
    public void setUp() {
        dateTime = LocalDateTime.now();
        dateTime2 = LocalDateTime.now().minusDays(1);
        quiz = new Quiz(1, "First Quiz", "First Constructor", dateTime);
        quiz2 = new Quiz(2, 2, "Second Quiz", "Second Constructor", dateTime);
        quiz3 = new Quiz(3, "Third Quiz", "Third Constructor", dateTime2, true, QuizDisplayType.MULTI_PAGE_QUESTION, true, true);
        quiz4 = new Quiz(4, 4, "Fourth Quiz", "Fourth Constructor", dateTime2, true, QuizDisplayType.MULTI_PAGE_QUESTION, true, true);
    }

    @Test
    public void testGetSetQuizId(){
        assertEquals(-1, quiz.getQuizId());
        assertEquals(2, quiz2.getQuizId());
        assertEquals(-1, quiz3.getQuizId());
        assertEquals(4, quiz4.getQuizId());
        quiz.setQuizId(1);
        quiz3.setQuizId(3);
        assertEquals(1, quiz.getQuizId());
        assertEquals(3, quiz3.getQuizId());
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> quiz2.setQuizId(5)
        );
        assertEquals("Quiz id can only be assigned once!", exception.getMessage());
        RuntimeException exception2 = assertThrows(
                RuntimeException.class,
                () -> quiz4.setQuizId(6)
        );
        assertEquals("Quiz id can only be assigned once!", exception2.getMessage());
    }

    @Test
    public void testGetSetCreatorUserId(){
        assertEquals(1, quiz.getCreatorUserId());
        assertEquals(2, quiz2.getCreatorUserId());
        assertEquals(3, quiz3.getCreatorUserId());
        assertEquals(4, quiz4.getCreatorUserId());
        quiz.setCreatorUserId(1000);
        quiz2.setCreatorUserId(3535);
        quiz3.setCreatorUserId(20);
        quiz4.setCreatorUserId(20000);
        assertEquals(1000, quiz.getCreatorUserId());
        assertEquals(3535, quiz2.getCreatorUserId());
        assertEquals(20, quiz3.getCreatorUserId());
        assertEquals(20000, quiz4.getCreatorUserId());
    }

    @Test
    public void testGetSetTitle(){
        assertEquals("First Quiz", quiz.getTitle());
        assertEquals("Second Quiz", quiz2.getTitle());
        assertEquals("Third Quiz", quiz3.getTitle());
        assertEquals("Fourth Quiz", quiz4.getTitle());
        quiz.setTitle("iejfejfejfejfefj");
        quiz2.setTitle("2029421041fnifnef{][");
        quiz3.setTitle("Fofe(F-2120412rn");
        quiz4.setTitle("nvmeiefej   39r91-");
        assertEquals("iejfejfejfejfefj", quiz.getTitle());
        assertEquals("2029421041fnifnef{][", quiz2.getTitle());
        assertEquals("Fofe(F-2120412rn", quiz3.getTitle());
        assertEquals("nvmeiefej   39r91-", quiz4.getTitle());
    }

    @Test
    public void testGetSetDescription(){
        assertEquals("First Constructor", quiz.getDescription());
        assertEquals("Second Constructor", quiz2.getDescription());
        assertEquals("Third Constructor", quiz3.getDescription());
        assertEquals("Fourth Constructor", quiz4.getDescription());
        quiz.setDescription("iejfejfejfejfefj");
        quiz2.setDescription("2029421041fnifnef{][");
        quiz3.setDescription("Fofe(F-2120412rn");
        quiz4.setDescription("nvmeiefej   39r91-");
        assertEquals("iejfejfejfejfefj", quiz.getDescription());
        assertEquals("2029421041fnifnef{][", quiz2.getDescription());
        assertEquals("Fofe(F-2120412rn", quiz3.getDescription());
        assertEquals("nvmeiefej   39r91-", quiz4.getDescription());
    }

    @Test
    public void testGetSetCreationDate(){
        assertEquals(dateTime, quiz.getCreationDate());
        assertEquals(dateTime, quiz2.getCreationDate());
        assertEquals(dateTime2, quiz3.getCreationDate());
        assertEquals(dateTime2, quiz4.getCreationDate());
        quiz.setCreationDate(dateTime2);
        quiz2.setCreationDate(dateTime2);
        quiz3.setCreationDate(dateTime);
        quiz4.setCreationDate(dateTime);
        assertEquals(dateTime2, quiz.getCreationDate());
        assertEquals(dateTime2, quiz2.getCreationDate());
        assertEquals(dateTime, quiz3.getCreationDate());
        assertEquals(dateTime, quiz4.getCreationDate());
    }

    @Test
    public void testGetSetQuizDisplayType(){
        assertEquals(QuizDisplayType.SINGLE_PAGE, quiz.getQuizDisplayType());
        assertEquals(QuizDisplayType.SINGLE_PAGE, quiz2.getQuizDisplayType());
        assertEquals(QuizDisplayType.MULTI_PAGE_QUESTION, quiz3.getQuizDisplayType());
        assertEquals(QuizDisplayType.MULTI_PAGE_QUESTION, quiz4.getQuizDisplayType());
        quiz.setQuizDisplayType(QuizDisplayType.MULTI_PAGE_QUESTION);
        quiz2.setQuizDisplayType(QuizDisplayType.MULTI_PAGE_QUESTION);
        quiz3.setQuizDisplayType(QuizDisplayType.SINGLE_PAGE);
        quiz4.setQuizDisplayType(QuizDisplayType.SINGLE_PAGE);
        assertEquals(QuizDisplayType.MULTI_PAGE_QUESTION, quiz.getQuizDisplayType());
        assertEquals(QuizDisplayType.MULTI_PAGE_QUESTION, quiz2.getQuizDisplayType());
        assertEquals(QuizDisplayType.SINGLE_PAGE, quiz3.getQuizDisplayType());
        assertEquals(QuizDisplayType.SINGLE_PAGE, quiz4.getQuizDisplayType());
    }
    @Test
    public void testGetSetIsRandomOrder(){
        assertFalse(quiz.isRandomOrder());
        assertFalse(quiz2.isRandomOrder());
        assertTrue(quiz3.isRandomOrder());
        assertTrue(quiz4.isRandomOrder());
        quiz.setRandomOrder(true);
        quiz2.setRandomOrder(true);
        quiz3.setRandomOrder(false);
        quiz4.setRandomOrder(false);
        assertTrue(quiz.isRandomOrder());
        assertTrue(quiz2.isRandomOrder());
        assertFalse(quiz3.isRandomOrder());
        assertFalse(quiz4.isRandomOrder());
    }

    @Test
    public void testGetSetIsImmediateCorrection(){
        assertFalse(quiz.isImmediateCorrection());
        assertFalse(quiz2.isImmediateCorrection());
        assertTrue(quiz3.isImmediateCorrection());
        assertTrue(quiz4.isImmediateCorrection());
        quiz.setImmediateCorrection(true);
        quiz2.setImmediateCorrection(true);
        quiz3.setImmediateCorrection(false);
        quiz4.setImmediateCorrection(false);
        assertTrue(quiz.isImmediateCorrection());
        assertTrue(quiz2.isImmediateCorrection());
        assertFalse(quiz3.isImmediateCorrection());
        assertFalse(quiz4.isImmediateCorrection());
    }

    @Test
    public void testGetSetIsPracticeModeEnabled(){
        assertFalse(quiz.isPracticeModeEnabled());
        assertFalse(quiz2.isPracticeModeEnabled());
        assertTrue(quiz3.isPracticeModeEnabled());
        assertTrue(quiz4.isPracticeModeEnabled());
        quiz.setPracticeModeEnabled(true);
        quiz2.setPracticeModeEnabled(true);
        quiz3.setPracticeModeEnabled(false);
        quiz4.setPracticeModeEnabled(false);
        assertTrue(quiz.isPracticeModeEnabled());
        assertTrue(quiz2.isPracticeModeEnabled());
        assertFalse(quiz3.isPracticeModeEnabled());
        assertFalse(quiz4.isPracticeModeEnabled());
    }

    @Test
    public void testIsSinglePage(){
        assertTrue(quiz.isSinglePage());
        assertTrue(quiz2.isSinglePage());
        assertFalse(quiz3.isSinglePage());
        assertFalse(quiz4.isSinglePage());
    }

    @Test
    public void testIsMultiPage(){
        assertFalse(quiz.isMultiPage());
        assertFalse(quiz2.isMultiPage());
        assertTrue(quiz3.isMultiPage());
        assertTrue(quiz4.isMultiPage());
    }

    @Test
    public void testEquals(){
        Quiz quizCopy = quiz;
        assertEquals(quiz, quizCopy);
        ArrayList<Boolean> randomCollection = new ArrayList<>();
        assertFalse(quiz.equals(randomCollection));
        Quiz newQuiz = new Quiz(1, 2, "New Quiz", "New Constructor", dateTime2, true, QuizDisplayType.MULTI_PAGE_QUESTION, true, true);
        List<Question> questions = new ArrayList<>();
        FillInTheBlankQuestion fillInTheBlankQuestion = new FillInTheBlankQuestion(1, "", null, quiz.getQuizId(), 1 , 1);
        questions.add(fillInTheBlankQuestion);
        newQuiz.setQuestions(questions);
        assertNotEquals(quiz, newQuiz);
        quiz.setQuizId(newQuiz.getQuizId());
        assertNotEquals(quiz, newQuiz);
        quiz.setCreatorUserId(newQuiz.getCreatorUserId());
        assertNotEquals(quiz, newQuiz);
        quiz.setTitle(newQuiz.getTitle());
        assertNotEquals(quiz, newQuiz);
        quiz.setDescription(newQuiz.getDescription());
        assertNotEquals(quiz, newQuiz);
        quiz.setCreationDate(newQuiz.getCreationDate());
        assertNotEquals(quiz, newQuiz);
        quiz.setRandomOrder(newQuiz.isRandomOrder());
        assertNotEquals(quiz, newQuiz);
        quiz.setQuizDisplayType(newQuiz.getQuizDisplayType());
        assertNotEquals(quiz, newQuiz);
        quiz.setImmediateCorrection(newQuiz.isImmediateCorrection());
        assertNotEquals(quiz, newQuiz);
        quiz.setPracticeModeEnabled(newQuiz.isPracticeModeEnabled());
        assertNotEquals(quiz, newQuiz);
        quiz.setQuestions(questions);
        assertEquals(quiz, newQuiz);
    }

    @Test
    public void testToString(){
        String s = quiz.getQuizId() + " " + quiz.getCreatorUserId() + " " + quiz.getTitle() + " " +
                quiz.getDescription() + " " + quiz.getCreationDate().toString() + " " + quiz.isRandomOrder()
                + " " + quiz.getQuizDisplayType().getDatabaseValue() + " " + quiz.isImmediateCorrection() + " " +
                quiz.isPracticeModeEnabled();
        assertEquals(s, quiz.toString());
    }

    //Tests getting and setting questions to the quiz
    @Test
    public void testGetSetQuestions() {
        Quiz quiz = new Quiz(1, "Test Quiz", "Description", LocalDateTime.now());
        assertNull(quiz.getQuestions());

        // Test setting questions
        List<Question> questions = new ArrayList<>();
        Map<String, Boolean> options = new HashMap<>();
        options.put("Option 1", true);
        options.put("Option 2", false);
        Question question1 = new MultipleChoiceQuestion("Question 1?", options, 1, 0, 1);
        Question question2 = new MultipleChoiceQuestion("Question 2?", options, 1, 1, 1);
        questions.add(question1);
        questions.add(question2);
        quiz.setQuestions(questions);
        List<Question> retrievedQuestions = quiz.getQuestions();
        assertNotNull(retrievedQuestions);
        assertEquals(2, retrievedQuestions.size());
        assertEquals(question1, retrievedQuestions.get(0));
        assertEquals(question2, retrievedQuestions.get(1));
        quiz.setQuestions(null);
        assertNull(quiz.getQuestions());
    }
}
