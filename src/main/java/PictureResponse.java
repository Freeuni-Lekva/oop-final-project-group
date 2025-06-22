
import java.util.HashSet;

public class PictureResponse {
    private String questionText;
    private int quizId;
    private HashSet<String> acceptableAnswers;
    private String imageUrl;
    private int orderInQuiz;

    public PictureResponse(String question, int quiz, HashSet<String> answers, String url, int order) {
        this.questionText = question;
        this.quizId = quiz;
        this.acceptableAnswers = answers;
        this.imageUrl = url;
        this.orderInQuiz = order;
    }

    public String getQuestionText() {
        return questionText;
    }

    public int getQuizId() {
        return quizId;
    }

    public HashSet<String> getAcceptableAnswers() {
        return acceptableAnswers;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getOrderInQuiz() {
        return orderInQuiz;
    }
}
