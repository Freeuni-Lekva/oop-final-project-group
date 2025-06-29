package models;

import java.util.*;

public class MultiAnswerQuestion extends Question {
    private final Map<String, Boolean> options;

    public MultiAnswerQuestion(int questionId, String questionText, Map<String, Boolean> options, int quizId, int orderInQuiz) {
        super(questionId, questionText, QuestionType.MULTI_ANSWER_UNORDERED, quizId, orderInQuiz);
        this.options = options;
    }

    public MultiAnswerQuestion(String questionText, Map<String, Boolean> options, int quizId, int orderInQuiz) {
        super(questionText, QuestionType.MULTI_ANSWER_UNORDERED, quizId, orderInQuiz);
        this.options = options;
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
            }
        }
        return correctAnswersCount / correctAnswers.size();
    }

    public Map<String, Boolean> getOptions() {
        return new HashMap<>(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiAnswerQuestion)) return false;
        MultiAnswerQuestion that = (MultiAnswerQuestion) o;
        return getQuestionId() == that.getQuestionId() && getQuizId() == that.getQuizId()
                && getQuestionType().equals(that.getQuestionType()) && getOptions().equals(that.getOptions()) &&
                getQuestionText().equals(that.getQuestionText()) && Objects.equals(getImageUrl(), that.getImageUrl()) &&
                getOrderInQuiz().equals(that.getOrderInQuiz());
    }
}