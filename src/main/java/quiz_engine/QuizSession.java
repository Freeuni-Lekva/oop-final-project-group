package quiz_engine;

import User.UserDao;
import dao.*;
import factory.QuestionDaoFactory;
import models.Question;
import models.Quiz;
import models.QuizAttempt;
import models.UserAnswer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages the state and progression of a user taking a quiz.
 * Handles question navigation, answer submission, progress tracking, scoring and quiz completion logic.
 * Supports both single-page and multi-page quiz modes with configurable settings for
 * question randomization, immediate correction, and backward navigation.
 * Maintains session state including current question position, user answers, and timing information.
 */
public class QuizSession {
    private final Quiz quiz;
    private final List<Question> questions;
    private int currentQuestion;
    private Map<Integer, List<String>> answers;
    private final Timestamp startTime;
    private final int attemptId;
    private final UserAnswerDao userAnswerDao;
    private final QuizAttemptDao quizAttemptDao;

    /**
     * Creates a new quiz session and initializes a quiz attempt in the database.
     * Validates user and quiz existence, creates the attempt record, loads questions,
     * and applies randomization if configured.
     * @param userId the ID of the user taking the quiz
     * @param quizId the ID of the quiz being taken
     * @throws IllegalArgumentException if user of quiz doesn't exist or quiz has not questions
     * @throws RuntimeException if quiz attemot creation fails
     */
    public QuizSession(int userId, int quizId) {
        this.userAnswerDao = new UserAnswerDao();
        this.quizAttemptDao = new QuizAttemptDao();
        QuizDao quizDao = new QuizDao();
        UserDao userDao = new UserDao();

        if (!quizDao.quizExists(quizId)) {
            throw new IllegalArgumentException("Quiz does not exist: " + quizId);
        }
        if (userDao.getUserById(userId) == null) {
            throw new IllegalArgumentException("User does not exist: " + userId);
        }
        if (quizDao.getQuestionCount(quizId) == 0) {
            throw new IllegalArgumentException("Quiz has no questions: " + quizId);
        }

        QuizAttempt quizAttempt = new QuizAttempt(userId, quizId);
        boolean wasCreated = quizAttemptDao.createAttempt(quizAttempt);
        if (!wasCreated) {
            throw new RuntimeException("Failed to create quiz attempt");
        }

        this.quiz = quizDao.getQuizById(quizId);
        this.questions = quiz.getQuestions();
        this.attemptId = quizAttempt.getAttemptId();
        this.answers = new HashMap<>();
        this.startTime = Timestamp.valueOf(LocalDateTime.now());
        this.currentQuestion = 0;

        if (quiz.isRandomOrder()) {
            Collections.shuffle(this.questions);
        }
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
     * Lets user submit their answers to the question.
     * Validates answers using the question's logic, stores them in session state,
     * and persists them to the database.
     * @param questionOrder for example, 5 for the fifth question(0-based)
     * @param userAnswers User's submitted answers for this question
     * @return true if answers were saved successfully, false otherwise
     */
    public boolean submitAnswer(int questionOrder, List<String> userAnswers){
        if(userAnswers == null || userAnswers.isEmpty()){
            return false;
        }

        try {
            answers.put(questionOrder, userAnswers);
            Question question = questions.get(questionOrder);
            List<Boolean> correctness = question.checkAnswers(userAnswers);

            userAnswerDao.deleteAnswersForQuestion(attemptId, question.getQuestionId());

            for (int i = 0; i < userAnswers.size(); i++) {
                String answer = userAnswers.get(i);
                boolean isCorrect = correctness.get(i);

                UserAnswer userAnswer = createUserAnswerForQuestion(question, answer, isCorrect);
                userAnswerDao.saveAnswer(userAnswer);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
     * @throws IllegalStateException if user tries to go back when going back is not
     * allowed for this quiz type
     */
    public void moveToPreviousQuestion(){
        if (!canGoBack()) {
            throw new IllegalStateException("Going back is not allowed for this quiz type");
        }
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
     * @throws IllegalStateException if user tries to go back when going back is not
     * allowed for this quiz type
     */
    public void moveToQuestion(int questionOrder) {
        if(questionOrder < currentQuestion){
            if(!canGoBack()){
                throw new IllegalStateException("Going back is not allowed for this quiz type");
            }
        }
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

    /**
     * Completes the quiz attempt by calculating the final score and updating the database.
     * Retrieves all submitted answers, groups them by question, calculates partial credit
     * using each question's scoring logic, and marks the attempt as completed.
     * @return the completed QuizAttempt with final score and timing, null if error occurs
     */
    public QuizAttempt completeQuiz() {
        try {
            List<UserAnswer> allAnswers = userAnswerDao.getAnswersForAttempt(attemptId);
            Map<Integer, List<UserAnswer>> answersByQuestion = groupAnswersByQuestionId(allAnswers);
            double totalScore = calculateTotalScore(answersByQuestion);
            quizAttemptDao.completeAttempt(attemptId, totalScore);
            return quizAttemptDao.getAttemptById(attemptId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Creates a UserAnswer object appropriate for the given question type.
     * Handles the different requirements of multiple choice questions (which need option_id)
     * versus text-based questions.
     */
    private UserAnswer createUserAnswerForQuestion(Question question, String answer, boolean isCorrect) {
        if (question.getQuestionType().equals("MULTIPLE_CHOICE") ||
                question.getQuestionType().equals("MULTI_ANSWER_UNORDERED")) {
            Integer optionId = findOptionId(question, answer);
            return new UserAnswer(attemptId, question.getQuestionId(), answer, optionId, isCorrect);
        } else {
            return new UserAnswer(attemptId, question.getQuestionId(), answer, null, isCorrect);
        }
    }

    /*
     * Finds the database option ID for a multiple choice answer.
     * Required for maintaining foreign key relationships in the UserAnswers table.
     */
    private Integer findOptionId(Question question, String answerText) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT option_id FROM AnswerOptionsMC WHERE question_id = ? AND option_text = ?")) {
            stmt.setInt(1, question.getQuestionId());
            stmt.setString(2, answerText);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("option_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Groups UserAnswer records by their question ID for scoring calculations.
     * Converts the flat list of individual answer pieces back into a question-grouped
     * structure that can be processed by each question's scoring logic.
     */
    private Map<Integer, List<UserAnswer>> groupAnswersByQuestionId(List<UserAnswer> allAnswers) {
        Map<Integer, List<UserAnswer>> answersByQuestion = new HashMap<>();
        for (UserAnswer answer : allAnswers) {
            int questionId = answer.getQuestionId();
            if (!answersByQuestion.containsKey(questionId)) {
                answersByQuestion.put(questionId, new ArrayList<>());
            }
            answersByQuestion.get(questionId).add(answer);
        }
        return answersByQuestion;
    }

    /*
     * Calculates the total score for the quiz attempt by summing individual question scores.
     * Reconstructs the user's answers for each question and delegates to the question's
     * built-in scoring logic. Supports partial credit with decimal scoring where each
     * question is worth a maximum of 1.0 points.
     */
    private double calculateTotalScore(Map<Integer, List<UserAnswer>> answersByQuestion) {
        double totalScore = 0.0;
        for (Integer questionId : answersByQuestion.keySet()) {
            Question question = getQuestionById(questionId);
            List<UserAnswer> userAnswers = answersByQuestion.get(questionId);
            List<String> answerStrings = new ArrayList<>();
            for (UserAnswer userAnswer : userAnswers) {
                answerStrings.add(userAnswer.getAnswerGivenText());
            }
            double questionScore = question.calculateScore(answerStrings);
            totalScore += questionScore;
        }
        return totalScore;
    }

    /*
     * Retrieves a question by its database ID.
     */
    private Question getQuestionById(int questionId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT question_type FROM Questions WHERE question_id = ?")) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String questionType = rs.getString("question_type");
                AbstractQuestionDao dao = QuestionDaoFactory.getDao(questionType);
                return dao.getQuestionById(questionId);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
