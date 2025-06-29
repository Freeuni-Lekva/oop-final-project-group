package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents a fill-in-the-blank question where users provide text answers.
 * Each blank can have multiple acceptable answers, and answers are checked
 * with normalized whitespace (trimmed and collapsed).
 * Example: "The capital of _____ is _____"
 * with answers [["France", "france"], ["Paris"]] for the two blanks.
 */
public class FillInTheBlankQuestion extends Question {
    /*
     * A list storing the correct answers, with each hashset
     * storing possible correct answers for one blank
     */
    private final List<HashSet<String>> answersList;

    public FillInTheBlankQuestion(int questionId, String questionText, List<HashSet<String>> answers, int quizId, int orderInQuiz) {
        super(questionId, questionText, QuestionType.FILL_IN_BLANK, quizId, orderInQuiz);
        this.answersList = answers;
    }

    public FillInTheBlankQuestion(String questionText, List<HashSet<String>> answers, int quizId, int orderInQuiz) {
        super(questionText, QuestionType.FILL_IN_BLANK, quizId, orderInQuiz);
        this.answersList = answers;
    }

    @Override
    public List<Boolean> checkAnswers(List<String> userAnswers) {
        checkUserAnswersException(userAnswers);
        List<Boolean> result = new ArrayList<>();
        for(int i = 0; i < userAnswers.size(); i++){
            if(answersList.get(i).contains(cleanString(userAnswers.get(i)))){
                result.add(true);
            }else{
                result.add(false);
            }
        }
        return result;
    }

    //Cleans the string from trimmed spaces
    private String cleanString(String str){
        return str.trim().replaceAll("\\s+", " ");
    }

    @Override
    public List <HashSet<String>> getCorrectAnswers() {
        return new ArrayList<>(answersList);
    }

    @Override
    public double calculateScore(List<String> userAnswers) {
        int correctAnswers = countCorrectAnswers(userAnswers);
        return (double)correctAnswers / (double)answersList.size();
    }

    //Counts how many correct answers where provided by the user
    private int countCorrectAnswers(List<String> userAnswers) {
        int count = 0;
        List<Boolean> answerCorrectnessList = checkAnswers(userAnswers);
        for(boolean bool : answerCorrectnessList){
            if(bool){
                count ++;
            }
        }
        return count;
    }

    /**
     * Throws an exception if a user has provided incorrect number of answers
     * @param userAnswers the answers of a user as List
     * @throws IllegalArgumentException
     */
    public void checkUserAnswersException(List<String> userAnswers)throws IllegalArgumentException{
        if(userAnswers.size() != answersList.size()){
            throw new IllegalArgumentException("Wrong number of user answers");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FillInTheBlankQuestion)) return false;
        FillInTheBlankQuestion that = (FillInTheBlankQuestion) o;
        return getQuestionId() == that.getQuestionId() && getQuizId() == that.getQuizId()
                && getQuestionType().equals(that.getQuestionType()) && getCorrectAnswers().equals(getCorrectAnswers()) &&
                getQuestionText().equals(that.getQuestionText()) && Objects.equals(getImageUrl(), that.getImageUrl()) &&
                getOrderInQuiz().equals(that.getOrderInQuiz());
    }
}