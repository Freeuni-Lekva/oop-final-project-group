package models;

import java.util.List;

/**
 * Abstract base class for representing a quiz question.
 * Defines common properties shared by all question types.
 * Subclasses must implement answer checking, scoring,
 * and provide type-specific answer formats.
 */
public abstract class Question {
    private final String questionText;
    private final QuestionType questionType;
    private int questionId;
    private final int quiz_Id;
    private String imageUrl;
    private Integer orderInQuiz;
    private double maxScore;

    /**
     * Default constructor, should be used when the question is first created and
     * the questionId is unknown. questionId is assigned -1.
     * @param questionText The question being asked
     * @param questionType The questionType
     * @param quiz_Id the id of a quiz the question belongs to
     * @param orderInQuiz which N is the question in a quiz
     */
    public Question(String questionText, QuestionType questionType, int quiz_Id, int orderInQuiz, double maxScore) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.quiz_Id = quiz_Id;
        this.questionId = -1;
        this.imageUrl = null;
        this.orderInQuiz = orderInQuiz;
        this.maxScore = maxScore;
    }

    /**
     * Constructor used when questionId is known
     * @param questionId The id of a question
     * @param questionText The question being asked
     * @param questionType The questionType
     * @param quiz_Id the id of a quiz the question belongs to
     * @param orderInQuiz which N is the question in a quiz
     */
    public Question(int questionId, String questionText, QuestionType questionType, int quiz_Id, int orderInQuiz, double maxScore) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.quiz_Id = quiz_Id;
        this.imageUrl = null;
        this.orderInQuiz = orderInQuiz;
        this.maxScore = maxScore;
    }

    /**
     * @return id of a question
     */
    public int getQuestionId() {
        return questionId;
    }

    /**
     * @return the question asked
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * @return The questionType
     */
    public String getQuestionType() {
        return this.questionType.getDatabaseValue();
    }

    /**
     * @return the id of a quiz the question belongs to
     */
    public int getQuizId() {
        return quiz_Id;
    }

    /**
     * @return the URL of an image in question
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @return which N is the question in a quiz
     */
    public Integer getOrderInQuiz() {
        return orderInQuiz;
    }

    /**
     * @return the maximum score that can be achieved answering this question
     */
    public double getMaxScore() {
        return maxScore;
    }
    /**
     * @param imageUrl the URL of an image to be included in a question
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @param orderInQuiz which N is the question in a quiz
     */
    public void setOrderInQuiz(Integer orderInQuiz) {
        this.orderInQuiz = orderInQuiz;
    }

    /**
     * Setter of question_id. It is usually called when the question_id is
     * inserted in the database and question_id gets assigned
     * CAN ONLY BE CALLED ONCE, WHEN QUESTION_ID IS NOT YET ASSIGNED
     * @param questionId the new id for the question
     */
    public void setQuestionId(int questionId) {
        if(this.questionId != -1){
            throw(new RuntimeException("Question id can only been assigned once!"));
        }
        this.questionId = questionId;
    }

    /**
     * Checks if the user's answers are correct for this question type.
     * @param userAnswers the list of user's submitted answers
     * @return list of boolean values indicating correctness for each answer
     */
    public abstract List<Boolean> checkAnswers(List<String> userAnswers);

    /**
     * Returns the correct answers in the format appropriate for this question type.
     * @return correct answers
     */
    public abstract Object getCorrectAnswers();

    /**
     * Calculates the score as a percentage of correct answers.
     * @param userAnswers the user's submitted answers
     * @return score between 0.0 and 1.0
     */
    public abstract double calculateScore(List<String> userAnswers);
}
