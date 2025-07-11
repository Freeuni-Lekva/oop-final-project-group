package models;

import java.util.*;

/**
 * Represents a multiple choice question with predefined multiple choice options.
 * Users select from available choices, and scoring requires all
 * selected answers to be correct (partial credit results in zero score).
 * Questions must have at least one correct option and at least one
 * incorrect option to be valid. Uses the "AnswerOptionsMC" table to store options with
 *  * their correctness flags.
 */
public class MultipleChoiceWithMultipleAnswersQuestion extends Question {
    //A map where answer option and their correctness is stored
    private final Map<String, Boolean> options;


    public MultipleChoiceWithMultipleAnswersQuestion(int questionId, String questionText, Map<String, Boolean> options, int quizId, int orderInQuiz, double maxScore) {
        super(questionId, questionText, QuestionType.MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS, quizId, orderInQuiz, maxScore);
        checkOptionsException(options);
        this.options = options;
    }

    public MultipleChoiceWithMultipleAnswersQuestion(String questionText, Map<String, Boolean> options, int quizId, int orderInQuiz, double maxScore) {
        super(questionText, QuestionType.MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS, quizId, orderInQuiz, maxScore);
        checkOptionsException(options);
        this.options = options;
    }

    /**
     * @param options the options Map with correctness flags
     * @throws IllegalArgumentException if all the options are true or false
     */
    public void checkOptionsException(Map<String, Boolean> options) throws IllegalArgumentException {
        int correctAnswers = 0;
        for(String option : options.keySet()) {
            if(options.get(option)) {
                correctAnswers++;
            }
        }
        if(correctAnswers == 0){
            throw new IllegalArgumentException("At least one option must be correct");
        }else if (correctAnswers == options.size()){
            throw new IllegalArgumentException("All options can't be correct");
        }
    }

    @Override
    public List<Boolean> checkAnswers(List<String> userAnswers) {
        checkUserAnswersException(userAnswers);
        List<Boolean> correctAnswers = new ArrayList<>();
        for(String answer : userAnswers) {
            if(options.get(answer)) {
                correctAnswers.add(true);
            }else{
                correctAnswers.add(false);
            }
        }
        return correctAnswers;
    }

    /**
     * Throws an exception if the user provided too many answers or
     * if he provided an answer that is not in the options.
     * @param userAnswers the answers provided by a user as a List
     * @throws IllegalArgumentException
     */
    public void checkUserAnswersException(List<String> userAnswers) throws IllegalArgumentException{
        if(userAnswers.size() > options.size()){
            throw new IllegalArgumentException("Wrong number of user answers!");
        }else{
            for(int i = 0; i < userAnswers.size(); i++){
                if(!options.containsKey(userAnswers.get(i))){
                    throw new IllegalArgumentException("Wrong answer at index " + i + "!");
                }
            }
        }
    }

    @Override
    public List<String> getCorrectAnswers() {
        List<String> correctAnswers = new ArrayList<>();
        for(String key : options.keySet()) {
            if(options.get(key)) {
                correctAnswers.add(key);
            }
        }
        return correctAnswers;
    }

    @Override
    public double calculateScore(List<String> userAnswers) {
        checkUserAnswersException(userAnswers);
        List<String> correctAnswers = getCorrectAnswers();
        double correctAnswersCount = 0;
        for(String userAnswer : userAnswers) {
            if(correctAnswers.contains(userAnswer)) {
                correctAnswersCount++;
            }else{
                return 0.0;
            }
        }
        return (correctAnswersCount / correctAnswers.size()) * getMaxScore();
    }

    /**
     * @return defensive copy of the options map
     */
    public Map<String, Boolean> getOptions() {
        return new HashMap<>(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultipleChoiceWithMultipleAnswersQuestion)) return false;
        MultipleChoiceWithMultipleAnswersQuestion that = (MultipleChoiceWithMultipleAnswersQuestion) o;
        return getQuestionId() == that.getQuestionId() && getQuizId() == that.getQuizId()
                && getQuestionType().equals(that.getQuestionType()) && getOptions().equals(that.getOptions()) &&
                getQuestionText().equals(that.getQuestionText()) && Objects.equals(getImageUrl(), that.getImageUrl()) &&
                getOrderInQuiz().equals(that.getOrderInQuiz());
    }
}
