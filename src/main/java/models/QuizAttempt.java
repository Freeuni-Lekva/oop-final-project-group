package models;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Represents a user's quiz attempt, either in-progress or completed.
 * For in-progress attempts: startTime and endTime are null, score is 0.
 * For completed attempts: all fields are populated with final results.
 * Used for tracking quiz history, performance analysis, and managing active sessions.
 */
public class QuizAttempt {
    private int attemptId;
    private final int userId;
    private final int quizId;
    private final Timestamp startTime;
    private final Timestamp endTime;
    private final double score;

    /**
     * Constructor for creating a new incomplete quiz attempt.
     * Used when starting a new quiz session before any questions are answered.
     * The database will assign the actual start_time, and end_time and score
     * will remain null/0 until the quiz is completed.
     * @param userId the ID of the user taking the quiz
     * @param quizId the ID of the quiz being taken
     */
    public QuizAttempt(int userId, int quizId) {
        this.attemptId = -1;
        this.userId = userId;
        this.quizId = quizId;
        this.startTime = null;
        this.endTime = null;
        this.score = 0;
    }

    /**
     * Constructor for creating a completed quiz attempt without a database-assigned ID.
     * Used when creating a completed attempt record that will be inserted into the database.
     * Typically used for historical data import or testing purposes.
     * @param userId the ID of the user who took the quiz
     * @param quizId the ID of the quiz that was taken
     * @param startTime timestamp when the quiz attempt began
     * @param endTime timestamp when the quiz attempt was completed
     * @param score the final score achieved (number of correct answers)
     */
    public QuizAttempt(int userId, int quizId, Timestamp startTime, Timestamp endTime, double score) {
        this.attemptId = -1;
        this.userId = userId;
        this.quizId = quizId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.score = score;
    }

    /**
     * Constructor for creating a quiz attempt with a known database ID.
     * Used when retrieving existing attempt records from the database.
     * @param attemptId the unique database ID for this attempt
     * @param userId the ID of the user who took the quiz
     * @param quizId the ID of the quiz that was taken
     * @param startTime timestamp when the quiz attempt began
     * @param endTime timestamp when the quiz attempt was completed
     * @param score the final score achieved (number of correct answers)
     */
    public QuizAttempt(int attemptId, int userId, int quizId, Timestamp startTime, Timestamp endTime, double score) {
        this(userId, quizId, startTime, endTime, score);
        this.attemptId = attemptId;
    }

    /**
     * @return the database ID of this attempt
     */
    public int getAttemptId(){
        return attemptId;
    }

    /**
     * @return the id of a user who took a quiz
     */
    public int getUserId(){
        return userId;
    }

    /**
     * @return the id of a quiz taken
     */
    public int getQuizId(){
        return quizId;
    }

    /**
     * @return the timestamp representing the time quiz was started
     */
    public Timestamp getStartTime(){
        return startTime;
    }

    /**
     * @return the timestamp representing the time quiz was ended
     */
    public Timestamp getEndTime(){
        return endTime;
    }

    /**
     * @return the final score achieved
     */
    public double getScore(){
        return score;
    }

    /**
     * @return the difference between endTime and startTime in seconds, or 0 if either is null
     */
    public int getTimeTakenSeconds(){
        if(startTime == null || endTime == null) {
            return 0;
        }
        return (int)(endTime.getTime() - startTime.getTime()) / 1000;
    }

    /**
     * Sets the attempt id, if it has not been assigned before
     * @param attemptId the attempt id
     * @throws RuntimeException throws a runtime exception if the attempt id has been assigned before (does not equal -1)
     */
    public void setAttemptId(int attemptId){
        if(this.attemptId != -1){
            throw new RuntimeException("Attempt id can only be assigned once!");
        }
        this.attemptId = attemptId;
    }

    /**
     * Overridden equals method, two objects equals if they are the same or if they are both
     * instance of QuizAttempt and have the same ivars.
     * @param o the object being compared to
     * @return a boolean indicating if two objects are equals or not
     */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof QuizAttempt)) return false;
        QuizAttempt that = (QuizAttempt) o;
        return attemptId == that.attemptId && userId == that.userId && quizId == that.quizId;
    }

    /**
     * Overridden to string method
     * @return ivars as a string separated by spaces
     */
    @Override
    public String toString(){
        return attemptId + " " + userId + " " + quizId + " " + startTime + " " + endTime + " " + score;
    }
}

