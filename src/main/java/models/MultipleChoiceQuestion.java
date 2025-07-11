package models;

import java.util.*;

/**
 * Represents a multiple choice question with exactly one correct answer.
 * Users can select only one option, and scoring is binary (1.0 for correct, 0.0 for incorrect).
 * Questions must have exactly one correct option to be valid.
 * Uses the "AnswerOptionsMC" table to store options with their correctness flags.
 */
public class MultipleChoiceQuestion extends Question {
    private final Map<String, Boolean> options;

    public MultipleChoiceQuestion(int questionId, String questionText, Map<String, Boolean> options, int quizId, int orderInQuiz) {
        super(questionId, questionText, QuestionType.MULTIPLE_CHOICE, quizId, orderInQuiz);
        validateSingleCorrectAnswer(options);
        this.options = options;
    }

    public MultipleChoiceQuestion(String questionText, Map<String, Boolean> options, int quizId, int orderInQuiz) {
        super(questionText, QuestionType.MULTIPLE_CHOICE, quizId, orderInQuiz);
        validateSingleCorrectAnswer(options);
        this.options = options;
    }

    /**
     * Checks if the single user answer is correct.
     * @param userAnswers list containing exactly one user answer
     * @return list with single boolean indicating if the answer is correct
     * @throws IllegalArgumentException if userAnswers doesn't contain exactly one valid answer
     */
    @Override
    public List<Boolean> checkAnswers(List<String> userAnswers) {
        checkUserAnswersException(userAnswers);
        List<Boolean> correctAnswers = new ArrayList<>();
        correctAnswers.add(options.get(userAnswers.get(0)));
        return correctAnswers;
    }

    /**
     * Validates user input to ensure exactly one answer is selected and it exists in options.
     * @param userAnswers the answers provided by a user as a List
     * @throws IllegalArgumentException if not exactly one answer or answer doesn't exist in options
     */
    public void checkUserAnswersException(List<String> userAnswers) throws IllegalArgumentException{
        if(userAnswers.size() != 1){
            throw new IllegalArgumentException("Must select exactly one answer!");
        }else{
            if (!options.containsKey(userAnswers.get(0))) {
                throw new IllegalArgumentException("Invalid answer option!");
            }
        }
    }

    /**
     * Gets all options marked as correct (should be exactly one).
     * @return list containing the single correct answer
     */
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

    /**
     * Calculates score for single-answer multiple choice question.
     * @param userAnswers list containing exactly one user answer
     * @return 1.0 if correct, 0.0 if incorrect
     * @throws IllegalArgumentException if userAnswers doesn't contain exactly one valid answer
     */
    @Override
    public double calculateScore(List<String> userAnswers) {
        checkUserAnswersException(userAnswers);
        String userAnswer = userAnswers.get(0);
        return options.get(userAnswer) ? 1.0 : 0.0;
    }

    /**
     * Returns defensive copy of the options map to prevent external modification.
     * @return defensive copy of the options map
     */
    public Map<String, Boolean> getOptions() {
        return new HashMap<>(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultipleChoiceQuestion)) return false;
        MultipleChoiceQuestion that = (MultipleChoiceQuestion) o;
        return getQuestionId() == that.getQuestionId() && getQuizId() == that.getQuizId()
                && getQuestionType().equals(that.getQuestionType()) && getOptions().equals(that.getOptions()) &&
                getQuestionText().equals(that.getQuestionText()) && Objects.equals(getImageUrl(), that.getImageUrl()) &&
                getOrderInQuiz().equals(that.getOrderInQuiz());
    }

    /*
     * Validates that exactly one correct answer exists in the options.
     */
    private void validateSingleCorrectAnswer(Map<String, Boolean> options) {
        int correctCount = 0;
        for (Boolean isCorrect : options.values()) {
            if (isCorrect) {
                correctCount++;
            }
        }
        if (correctCount != 1) {
            throw new IllegalArgumentException("Multiple choice questions must have exactly one correct answer");
        }
    }
}