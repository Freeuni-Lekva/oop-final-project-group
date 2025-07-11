package models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents a quiz with configurable settings and properties.
 * A quiz contains multiple questions and defines how they should be presented
 * to users (single page vs multipage, random order, immediate correction, practice mode...).
 * Quizzes are created by users and can be taken by other users.
 */
public class Quiz {
    private int quizId;
    private int creatorUserId;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private boolean isRandomOrder;
    private QuizDisplayType quizDisplayType;
    private boolean isImmediateCorrection;
    private boolean isPracticeModeEnabled;
    private List<Question> questions;

    /**
     * Default constructor, should be used when the quiz is first created and
     * the quizId is unknown. quiz is assigned -1. All the quiz settings are
     * set to their default values
     * @param creatorUserId the id of a user who created a quiz
     * @param title the title of a quiz
     * @param description the description of a quiz
     * @param creationDate the date when the quiz was created
     */
    public Quiz(int creatorUserId, String title, String description, LocalDateTime creationDate) {
        this.quizId = -1;
        this.creatorUserId = creatorUserId;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        isRandomOrder = false;
        quizDisplayType = QuizDisplayType.SINGLE_PAGE;
        isImmediateCorrection = false;
        isPracticeModeEnabled = false;
    }

    /**
     * Constructor used when questionId is known.
     * All the quiz settings are set to their default values
     * @param quizId the id of a quiz
     * @param creatorUserId the id of a user who created a quiz
     * @param title the title of a quiz
     * @param description the description of a quiz
     * @param creationDate the date when the quiz was created
     */
    public Quiz(int quizId, int creatorUserId, String title, String description, LocalDateTime creationDate) {
        this(creatorUserId, title, description, creationDate);
        this.quizId = quizId;
    }

    /**
     * Constructor used when the quiz is first created and the quizId is unknown.
     * Also, The user provides all the quiz option settings
     * @param creatorUserId the id of a user who created a quiz
     * @param title the title of a quiz
     * @param description the description of a quiz
     * @param creationDate the date when the quiz was created
     * @param isRandomOrder specifies if the order of questions should be randomized or not
     * @param quizDisplayType specifies if all the questions should appear on single page or on multiple page
     * @param isImmediateCorrection  specifies if the questions in the quiz are graded immediately or not
     * @param isPracticeModeEnabled Specifies if the quiz is being taken in practice mode or not
     */
    public Quiz(int creatorUserId, String title, String description, LocalDateTime creationDate,
                boolean isRandomOrder, QuizDisplayType quizDisplayType, boolean isImmediateCorrection, boolean isPracticeModeEnabled) {
        this(creatorUserId, title, description, creationDate);
        this.isRandomOrder = isRandomOrder;
        this.quizDisplayType = quizDisplayType;
        this.isImmediateCorrection = isImmediateCorrection;
        this.isPracticeModeEnabled = isPracticeModeEnabled;
    }

    /**
     * Constructor used when questionId is known and user wants to set
     * all the quiz option settings in constructor
     * @param quizId the id of a quiz
     * @param creatorUserId the id of a user who created a quiz
     * @param title the title of a quiz
     * @param description the description of a quiz
     * @param creationDate the date when the quiz was created
     * @param isRandomOrder specifies if the order of questions should be randomized or not
     * @param quizDisplayType specifies if all the questions should appear on single page or on multiple page
     * @param isImmediateCorrection  specifies if the questions in the quiz are graded immediately or not
     * @param isPracticeModeEnabled Specifies if the quiz is being taken in practice mode or not
     */
    public Quiz(int quizId, int creatorUserId, String title, String description, LocalDateTime creationDate,
                boolean isRandomOrder, QuizDisplayType quizDisplayType, boolean isImmediateCorrection, boolean isPracticeModeEnabled) {
        this(creatorUserId, title, description, creationDate, isRandomOrder, quizDisplayType, isImmediateCorrection, isPracticeModeEnabled);
        this.quizId = quizId;
    }

    public int getQuizId() {
        return quizId;
    }

    /**
     * Quiz id setter. Works only if the quiz id has not been set before (equals -1)
     * @param quizId The id of a quiz
     * @throws RuntimeException throws a Runtime exception if the quiz id was not -1
     */
    public void setQuizId(int quizId) {
        if(this.quizId == -1){
            this.quizId = quizId;
        }else{
            throw(new RuntimeException("Quiz id can only be assigned once!"));
        }
    }

    public int getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(int creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isRandomOrder() {
        return isRandomOrder;
    }

    public void setRandomOrder(boolean isRandomOrder) {
        this.isRandomOrder = isRandomOrder;
    }

    public QuizDisplayType getQuizDisplayType() {
        return quizDisplayType;
    }

    public void setQuizDisplayType(QuizDisplayType quizDisplayType) {
        this.quizDisplayType = quizDisplayType;
    }

    public boolean isImmediateCorrection() {
        return isImmediateCorrection;
    }

    public void setImmediateCorrection(boolean isImmediateCorrection) {
        this.isImmediateCorrection = isImmediateCorrection;
    }

    public boolean isPracticeModeEnabled() {
        return isPracticeModeEnabled;
    }

    public void setPracticeModeEnabled(boolean isPracticeModeEnabled) {
        this.isPracticeModeEnabled = isPracticeModeEnabled;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * A helper method which returns if the quiz is single page or not
     * @return a boolean indicating if the quiz is single page or not
     */
    public boolean isSinglePage() {
        return quizDisplayType == QuizDisplayType.SINGLE_PAGE;
    }

    /**
     * A helper method which returns if the quiz is multipage or not
     * @return a boolean indicating if the quiz is multipage or not
     */
    public boolean isMultiPage() {
        return quizDisplayType == QuizDisplayType.MULTI_PAGE_QUESTION;
    }

    /**
     * Overridden equals class. Two quizzes are equal if all the parameters and options
     * are equal
     * @param o the object being compared to
     * @return if two quizzes equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return quizId == quiz.quizId && creatorUserId == quiz.creatorUserId && title.equals(quiz.title) && description.equals(quiz.description)
                && creationDate.equals(quiz.creationDate) && isRandomOrder == quiz.isRandomOrder && quizDisplayType == quiz.quizDisplayType
                && isImmediateCorrection == quiz.isImmediateCorrection && isPracticeModeEnabled == quiz.isPracticeModeEnabled && Objects.equals(questions, quiz.questions);
    }

    /**
     * Overridden to string class
     * @return String which contains all the instance variables
     */
    @Override
    public String toString() {
        return quizId + " " + creatorUserId + " " + title + " " + description + " " + creationDate.toString() + " " + isRandomOrder
                + " " + quizDisplayType.getDatabaseValue() + " " + isImmediateCorrection + " " + isPracticeModeEnabled;
    }
}


