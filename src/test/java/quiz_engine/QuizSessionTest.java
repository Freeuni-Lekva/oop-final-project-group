package quiz_engine;

import dao.DatabaseConnection;
import dao.QuizDao;
import models.Question;
import models.Quiz;
import org.junit.jupiter.api.*;
import service.QuestionService;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizSessionTest {
    private Connection connection;
    private int userId;
    private Quiz quiz;
    private Quiz quiz2;
    private QuizSession quizSession;
    private QuizSession quizSession2;
    private List<Question> questions;

    private static final int ATTEMPT_ONE = 1;
    private static final int ATTEMPT_ONE_THOUSAND = 1001;

    //Sets up the databases
    @BeforeEach
    public void setUp() throws SQLException {

        //Creates a connection
        connection = DatabaseConnection.getConnection();

        //Creates a user which owns(creates) the quizzes
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Users (username, email, password_hash, salt) " +
                        "VALUES ('testuser', 'test@example.com', 'testhash', 'testsalt')",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        userId = keys.getInt(1);

        //Creates First Quiz
        stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title, description, creation_date, is_random_order, display_type, is_immediate_correction, is_practice_mode_enabled) " +
                        "VALUES (?, 'Basic Quiz', 'Test Description', NOW(), false, 'SINGLE_PAGE', false, false)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int testQuizId = keys.getInt(1);
        addAnswers(testQuizId);

        //Creates a second quiz
        stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title, description, creation_date, is_random_order, display_type, is_immediate_correction, is_practice_mode_enabled) " +
                        "VALUES (?, 'Restrictive Quiz', 'Test Description', NOW(), true, 'MULTI_PAGE_QUESTION', true, false)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int testQuizId2 = keys.getInt(1);
        addAnswers(testQuizId2);

        //Initializes the quizzes and quizSessions
        QuizDao quizDao = new QuizDao();
        quiz = quizDao.getQuizById(testQuizId);
        quiz2 = quizDao.getQuizById(testQuizId2);
        quizSession = new QuizSession(quiz, ATTEMPT_ONE);
        quizSession2 = new QuizSession(quiz2, ATTEMPT_ONE_THOUSAND);

        //Initializes the questions
        QuestionService questionService = new QuestionService();
        questions = questionService.getAllQuestionsFromQuiz(testQuizId);

        keys.close();
        stmt.close();
    }

    //Helper method, adds the questions and the answers to the quizzes
    private void addAnswers(int testQuizId) throws SQLException {
        //Creates a multiple choice question for the first quiz
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'Which countries are in Europe?', 'MULTIPLE_CHOICE', 1)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        int multipleChoiceQuestionId = keys.getInt(1);

        //Creates answers to the first multiple choice question
        stmt = connection.prepareStatement(
                "INSERT INTO AnswerOptions (question_id, option_text, is_correct) VALUES (?, ?, ?)"
        );
        stmt.setInt(1, multipleChoiceQuestionId);
        stmt.setString(2, "Germany");
        stmt.setBoolean(3, true);
        stmt.executeUpdate();
        stmt.setInt(1, multipleChoiceQuestionId);
        stmt.setString(2, "Brazil");
        stmt.setBoolean(3, false);
        stmt.executeUpdate();

        //Creates a fill in the blank question for the first quiz
        stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'The capital of Georgia is _____.', 'FILL_IN_BLANK', 2)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int fillInBlankQuestionId = keys.getInt(1);

        //Adds answers to the fill in the blank question
        stmt = connection.prepareStatement(
                "INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (?, ?, ?)"
        );
        stmt.setInt(1, fillInBlankQuestionId);
        stmt.setInt(2, 0);
        stmt.setString(3, "Tbilisi");
        stmt.executeUpdate();

        //Creates one more multiple choice question for the first quiz
        stmt = connection.prepareStatement(
                "INSERT INTO Questions (quiz_id, question_text, question_type, order_in_quiz) " +
                        "VALUES (?, 'Which countries are not in Europe?', 'MULTIPLE_CHOICE', 3)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        keys = stmt.getGeneratedKeys();
        keys.next();
        int multipleChoiceQuestionId2 = keys.getInt(1);

        //Adds answers to the multiple choice question
        stmt = connection.prepareStatement(
                "INSERT INTO AnswerOptions (question_id, option_text, is_correct) VALUES (?, ?, ?)"
        );
        stmt.setInt(1, multipleChoiceQuestionId2);
        stmt.setString(2, "Brazil");
        stmt.setBoolean(3, true);
        stmt.executeUpdate();
        stmt.setInt(1, multipleChoiceQuestionId2);
        stmt.setString(2, "Germany");
        stmt.setBoolean(3, false);
        stmt.executeUpdate();
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

    @Test
    public void testGettersAndSetters() {
        assertEquals(quiz, quizSession.getQuiz());
        assertEquals(quiz2, quizSession2.getQuiz());
        assertEquals(questions, quizSession.getQuestions());
        assertNotSame(questions, quizSession.getQuestions());
        assertEquals(ATTEMPT_ONE, quizSession.getAttemptId());
        assertEquals(ATTEMPT_ONE_THOUSAND, quizSession2.getAttemptId());
        assertEquals(0, quizSession.getCurrentQuestionId());
        assertEquals(0, quizSession2.getCurrentQuestionId());
        assertEquals("Question 1 of 3", quizSession.getQuizProgress());
        assertEquals("Question 1 of 3", quizSession2.getQuizProgress());
        assertTrue(quizSession.getAnswers().isEmpty());
        assertTrue(quizSession2.getAnswers().isEmpty());
        Question currentQuestionQuiz = quizSession.getCurrentQuestion();
        assertTrue(questions.contains(currentQuestionQuiz));
        Question currentQuestionQuiz2 = quizSession.getCurrentQuestion();
        assertTrue(questions.contains(currentQuestionQuiz2));
    }

    @Test
    public void testGetElapsedTime() throws InterruptedException {
        long firstCheck = quizSession.getElapsedTime();

        Thread.sleep(100);

        long secondCheck = quizSession.getElapsedTime();

        // Elapsed time should have increased
        assertTrue(secondCheck > firstCheck);
        assertTrue(secondCheck - firstCheck >= 100); // At least 100ms difference
    }

    @Test
    public void testGetStartTime(){
        Timestamp startTime = quizSession.getStartTime();
        assertNotNull(startTime);
        long timeDiff = System.currentTimeMillis() - startTime.getTime();
        assertTrue(timeDiff >= 0);
        assertTrue(timeDiff < 5000);
    }

    @Test
    public void testNavigation(){
        //Testing if going back is possible or not
        assertTrue(quizSession.canGoBack());
        assertFalse(quizSession2.canGoBack());

        //Testing going to prev question when on question 0
        assertFalse(quizSession.hasPreviousQuestion());
        assertFalse(quizSession2.hasPreviousQuestion());
        IndexOutOfBoundsException exception = assertThrows(
                IndexOutOfBoundsException.class,
                () -> quizSession.moveToPreviousQuestion()
        );
        assertEquals("There is no previous question", exception.getMessage());

        //Moving onto next question, testing if correct current ids and if question has previous question
        quizSession.moveToNextQuestion();
        quizSession2.moveToNextQuestion();
        assertEquals(1, quizSession.getCurrentQuestionId());
        assertEquals(1, quizSession2.getCurrentQuestionId());
        assertTrue(quizSession.hasPreviousQuestion());
        assertTrue(quizSession2.hasPreviousQuestion());
        quizSession.moveToPreviousQuestion();

        //Second quiz cant go to the previous question
        IllegalStateException exception2 = assertThrows(
                IllegalStateException.class,
                () -> quizSession2.moveToPreviousQuestion()
        );
        assertEquals("Going back is not allowed for this quiz type", exception2.getMessage());
        assertEquals(0, quizSession.getCurrentQuestionId());
        assertEquals(1, quizSession2.getCurrentQuestionId());

        //Starting to test moveToQuestion. question 2 still can't go back
        quizSession.moveToQuestion(1);
        assertEquals(1, quizSession.getCurrentQuestionId());
        IllegalStateException exception3 = assertThrows(
                IllegalStateException.class,
                () -> quizSession2.moveToQuestion(0)
        );
        assertEquals("Going back is not allowed for this quiz type", exception3.getMessage());
        assertEquals(1, quizSession2.getCurrentQuestionId());

        //Testing moving out of index
        IndexOutOfBoundsException exception4 = assertThrows(
                IndexOutOfBoundsException.class,
                () -> quizSession.moveToQuestion(4)
        );
        assertEquals("This question does not exist", exception4.getMessage());
        IndexOutOfBoundsException exception5 = assertThrows(
                IndexOutOfBoundsException.class,
                () -> quizSession.moveToQuestion(-1)
        );
        assertEquals("This question does not exist", exception5.getMessage());

        //Testing hasNextQuestion
        assertEquals(1, quizSession.getCurrentQuestionId());
        quizSession.moveToQuestion(2);
        quizSession2.moveToQuestion(1);
        assertFalse(quizSession.hasNextQuestion());
        assertTrue(quizSession2.hasNextQuestion());

        //Testing indexOutOfBounds exception on moveToNextQuestion
        IndexOutOfBoundsException exception6 = assertThrows(
                IndexOutOfBoundsException.class,
                () -> quizSession.moveToNextQuestion()
        );
        assertEquals("There is no next question", exception6.getMessage());
        assertEquals(2, quizSession.getCurrentQuestionId());
        assertEquals(1, quizSession2.getCurrentQuestionId());
        assertEquals("Question 3 of 3", quizSession.getQuizProgress());
        assertEquals("Question 2 of 3", quizSession2.getQuizProgress());
    }

    //Tests submitAnswers, isQuizFinished and getAnswers
    @Test
    public void testSubmitAnswers(){
        assertFalse(quizSession.isQuizFinished());
        List<String> answers = new ArrayList<>();
        answers.add("Germany");
        quizSession.submitAnswer(0, answers);
        assertFalse(quizSession.isQuizFinished());
        List<String> answers2 = new ArrayList<>();
        answers2.add("Brazil");
        quizSession.submitAnswer(2, answers2);
        assertFalse(quizSession.isQuizFinished());
        List<String> answers3 = new ArrayList<>();
        answers3.add("Tbilisi");
        quizSession.submitAnswer(1, answers3);
        assertTrue(quizSession.isQuizFinished());
        Map<Integer, List<String> > quizAnswers = quizSession.getAnswers();
        assertEquals(answers, quizAnswers.get(0));
        assertEquals(answers3, quizAnswers.get(1));
        assertEquals(answers2, quizAnswers.get(2));
    }

    //Tests if quizSession throws the correct exception if quiz has no questions
    @Test
    public void testEmptyQuizException() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title, description, creation_date, is_random_order, display_type, is_immediate_correction, is_practice_mode_enabled) " +
                        "VALUES (?, 'Empty Quiz', 'Test Description', NOW(), false, 'SINGLE_PAGE', false, false)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        int emptyQuizId = keys.getInt(1);
        QuizDao quizDao = new QuizDao();
        Quiz emptyQuiz = quizDao.getQuizById(emptyQuizId);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuizSession(emptyQuiz, ATTEMPT_ONE)
        );
        assertEquals("Quiz must have at least one question", exception.getMessage());

        keys.close();
        stmt.close();
    }
}
