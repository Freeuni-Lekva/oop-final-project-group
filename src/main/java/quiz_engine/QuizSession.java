package quiz_engine;

import models.Question;
import models.Quiz;
import service.QuestionService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages the state and progression of a user taking a quiz.
 * Handles question navigation, answer submission, progress tracking, and quiz completion logic.
 * Supports both single-page and multi-page quiz modes with configurable settings for
 * question randomization, immediate correction, and backward navigation.
 * Maintains session state including current question position, user answers, and timing information.
 */
public class QuizSession {
    private final Quiz quiz;
    private final List<Question> questions;
    private int currentQuestion;
    private Map<Integer, List<String>> answers;
    private Timestamp startTime;
    private final int attemptId;
    private final QuestionService questionService;

    /**
     * Creates a new quiz session for the specified quiz and attempt.
     * Loads all questions from the quiz and applies randomization if configured.
     * @param quiz the quiz being taken
     * @param attemptId the database ID for this quiz attempt
     * @throws IllegalArgumentException if the quiz has no questions
     */
    public QuizSession(Quiz quiz, int attemptId) {
        questionService = new QuestionService();
        this.questions = questionService.getAllQuestionsFromQuiz(quiz.getQuizId());
        if (this.questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz must have at least one question");
        }
        if (quiz.isRandomOrder()) {
            Collections.shuffle(this.questions);
        }
        this.quiz = quiz;
        this.attemptId = attemptId;
        this.answers = new HashMap<>();
        this.startTime = Timestamp.valueOf(LocalDateTime.now());
        this.currentQuestion = 0;
    }

    /**
     * @return the quiz being taken
     */
    public Quiz getQuiz() {
        return quiz;
    }

    /**
     * @return defensive copy of all the questions in a list
     */
    public List<Question> getQuestions() {
        return new ArrayList<>(questions);
    }

    /**
     * @return the id of a quiz being taken
     */
    public int getCurrentQuestionId() {
        return currentQuestion;
    }

    /**
     * @return the map containing question order as keys and user's submitted
     * answers as values
     */
    public Map<Integer, List<String>> getAnswers() {
        return answers;
    }

    /**
     * @return the timestamp for time when the quiz was started
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * @return the id of a current attempt
     */
    public int getAttemptId() {
        return attemptId;
    }

    /**
     * @return the current question being taken
     */
    public Question getCurrentQuestion() {
        return questions.get(currentQuestion);
    }

    /**
     * lets user submit their answers to the question
     * @param questionOrder for example, 5 for the fifth question
     * @param answer
     */
    public void submitAnswer(int questionOrder, List<String> answer){
        answers.put(questionOrder, answer);
    }

    /**
     * @return boolean indicating if the question has a next question or
     * is it a last question
     */
    public boolean hasNextQuestion(){
        return currentQuestion < questions.size() - 1;
    }

    /**
     * @return boolean indicating if the question has a previous question or
     * is it a first question
     */
    public boolean hasPreviousQuestion(){
        return currentQuestion > 0;
    }

    /**
     * moves to the next question by incrementing current question
     * @throws IndexOutOfBoundsException if there is no next question
     */
    public void moveToNextQuestion(){
        if(hasNextQuestion()){
            currentQuestion++;
        }else{
            throw new IndexOutOfBoundsException("There is no next question");
        }
    }

    /**
     * moves to the previous question by decrementing current question
     * @throws IndexOutOfBoundsException if there is no previous question
     */
    public void moveToPreviousQuestion(){
        if(hasPreviousQuestion()){
            currentQuestion--;
        }else{
            throw new IndexOutOfBoundsException("There is no previous question");
        }
    }

    /**
     * @return boolean indicating if all the questions were answered
     */
    public boolean isQuizFinished() {
        for(int i = 0; i < questions.size(); i++){
            if(!answers.containsKey(i)){
                return false;
            }
        }
        return true;
    }

    /**
     * @return the time that has passed from the start of a quiz
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime.getTime();
    }

    /**
     * moves to the question specified by the user
     * @throws IndexOutOfBoundsException if there is no question with that questionOrder
     */
    public void moveToQuestion(int questionOrder) {
        if(questionOrder < 0 || questionOrder >= questions.size()){
            throw new IndexOutOfBoundsException("This question does not exist");
        }else{
            currentQuestion = questionOrder;
        }
    }

    /**
     * @return the string indicating which question out of how many is being taken
     */
    public String getQuizProgress(){
        return "Question " + (currentQuestion + 1) + " of " + questions.size();
    }

    /**
     * @return boolean indicating if previous questions can be revisited and
     * answers can be changed depending on the quiz settings
     */
    public boolean canGoBack() {
        if(quiz.isSinglePage()){
            return true;
        }else return !quiz.isImmediateCorrection();
    }
}
