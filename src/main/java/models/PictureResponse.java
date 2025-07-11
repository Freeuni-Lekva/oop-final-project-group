package models;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/*
* bean class for picture response type questions
* the user enters an answer based on the provided image and question(Example image: George Washington, question; who is shown on the image)*
* the resulting points to this type of question are binary(either 0 or 1)
* there may be multiple allowed answers(Example: "George Washington", Washington, "the first president of the US" and etc.)
* */
public class PictureResponse extends Question {
    private HashSet<String> allowedAnswers;

    public PictureResponse(String questionText, QuestionType questionType, int quiz_Id, int orderInQuiz, String imageUrl, HashSet<String> allowedAnswers, double maxScore) {
        super(questionText, questionType, quiz_Id, orderInQuiz, maxScore);
        this.setImageUrl(imageUrl);
        this.allowedAnswers = allowedAnswers;
    }

    public PictureResponse(int questionId,String questionText, QuestionType questionType, int quiz_Id, int orderInQuiz, String imageUrl, HashSet<String> allowedAnswers, double maxScore) {
        super(questionId, questionText, questionType, quiz_Id, orderInQuiz, maxScore);
        this.setImageUrl(imageUrl);
        this.allowedAnswers = allowedAnswers;
    }

    public PictureResponse(int questionId, String questionText, QuestionType questionType, int quizId, int orderInQuiz, HashSet<String> allowedAnswers, double maxScore) {
        super(questionId, questionText, questionType, quizId, orderInQuiz, maxScore);
        this.allowedAnswers = allowedAnswers;
    }

    @Override
    public List<Boolean> checkAnswers(List<String> userAnswers) {
        List<Boolean> answers = new ArrayList<>();
        for (String userAnswer : userAnswers) {
            if (!allowedAnswers.contains(cleanString(userAnswer))) {
                answers.add(false);
            }else{
                answers.add(true);
            }
        }
        return answers;
    }

    @Override
    public Object getCorrectAnswers() {
        return allowedAnswers;
    }

    @Override
    public double calculateScore(List<String> userAnswers) {
        for (String userAnswer : userAnswers) {
            if (!allowedAnswers.contains(userAnswer)) {
                return 0.0;
            }
        }
        return getMaxScore();
    }

    //Cleans the string from trimmed spaces
    private String cleanString(String str){
        return str.trim().replaceAll("\\s+", " ");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureResponse that = (PictureResponse) o;
        return this.getQuestionId() == that.getQuestionId() &&
                this.getQuizId() == that.getQuizId() &&
                Objects.equals(this.getQuestionText(), that.getQuestionText()) &&
                Objects.equals(this.getQuestionType(), that.getQuestionType()) &&
                Objects.equals(this.getImageUrl(), that.getImageUrl()) &&
                Objects.equals(this.getOrderInQuiz(), that.getOrderInQuiz()) &&
                allowedAnswers.equals(that.allowedAnswers);
    }

}
