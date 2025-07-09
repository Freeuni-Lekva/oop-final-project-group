package models;

import java.util.Objects;

/**
 * Represents a user's answer to a specific question within a quiz attempt.
 * Each UserAnswer record stores one individual answer piece - for questions with
 * multiple parts (like fill-in-blank with multiple blanks or multiple-choice with
 * multiple selections), there will be multiple UserAnswer records per question.
 * Used for tracking quiz history, performance analysis, and answer review.
 */
public class UserAnswer {
    private int userAnswerId;
    private final int attemptId;
    private final int questionId;
    private final String answerGivenText;
    private final Integer selected_option_id;
    private final boolean isCorrect;

    /**
     * Constructor for creating a new user answer without a database-assigned ID.
     * Used when creating a new answer record that will be inserted into the database.
     * The userAnswerId will be set to -1 and should be assigned later using setUserAnswerId().
     * @param attemptId the ID of the quiz attempt this answer belongs to
     * @param questionId the ID of the question being answered
     * @param answerGivenText the text answer provided by the user
     * @param selected_option_id the ID of the selected option (null for text answers)
     * @param isCorrect whether this individual answer piece was correct
     */
    public UserAnswer(int attemptId, int questionId, String answerGivenText, Integer selected_option_id, boolean isCorrect) {
        this.userAnswerId = -1;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.answerGivenText = answerGivenText;
        this.selected_option_id = selected_option_id;
        this.isCorrect = isCorrect;
    }

    /**
     * Constructor for creating a user answer with a known database ID.
     * Used when retrieving existing answer records from the database.
     * @param userAnswerId the unique database ID for this answer record
     * @param attemptId the ID of the quiz attempt this answer belongs to
     * @param questionId the ID of the question being answered
     * @param answerGivenText the text answer provided by the user
     * @param selected_option_id the ID of the selected option (null for text answers)
     * @param isCorrect whether this individual answer piece was correct
     */
    public UserAnswer(int userAnswerId, int attemptId, int questionId, String answerGivenText, Integer selected_option_id, boolean isCorrect) {
        this(attemptId, questionId, answerGivenText, selected_option_id, isCorrect);
        this.userAnswerId = userAnswerId;
    }

    /**
     * @return returns the user answer id
     */
    public int getUserAnswerId() {
        return userAnswerId;
    }

    /**
     * Sets the user answer id, if it has not been assigned before
     * @param userAnswerId the user answer id
     * @throws RuntimeException throws a runtime exception if the user answer id has been assigned before (does not equal -1)
     */
    public void setUserAnswerId(int userAnswerId) {
        if(this.userAnswerId != -1) {
            throw new RuntimeException("User Answer Id can only be assigned once!");
        }else{
            this.userAnswerId = userAnswerId;
        }
    }

    /**
     * @return returns the attempt id
     */
    public int getAttemptId() {
        return attemptId;
    }

    /**
     * @return returns the question id
     */
    public int getQuestionId(){
        return questionId;
    }

    /**
     * @return returns the text answer provided by the user
     */
    public String getAnswerGivenText() {
        return answerGivenText;
    }

    /**
     * @return returns the ID of the selected option (null for text answers)
     */
    public Integer getSelectedOptionId() {
        return selected_option_id;
    }

    /**
     * @return returns whether this individual answer piece was correct
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * @return returns overridden toString which is a String containing ivars separated by the spaces
     */
    @Override
    public String toString() {
        return userAnswerId + " " + attemptId + " " + questionId + " " +
                answerGivenText + " " + selected_option_id + " " + isCorrect;
    }

    /**
     * Overridden equals object, two userAnswers are equals if the two objects are the same
     * or if the object being compared to is an instance of UserAnswer and all the ivars are
     * equals
     * @param o the object being compared to
     * @return boolean indicating if the two objects are equals or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof UserAnswer)) return false;
        UserAnswer userAnswer = (UserAnswer) o;
        return userAnswerId == userAnswer.userAnswerId && attemptId == userAnswer.attemptId
                && questionId == userAnswer.questionId && answerGivenText.equals(userAnswer.answerGivenText)
                && Objects.equals(selected_option_id, userAnswer.selected_option_id) && isCorrect == userAnswer.isCorrect;
    }
}
