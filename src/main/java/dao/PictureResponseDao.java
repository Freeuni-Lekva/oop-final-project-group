package dao;

import models.Question;
import models.PictureResponse;
import models.QuestionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class PictureResponseDao extends AbstractQuestionDao{
    @Override
    protected Object getAnswersFromDB(Connection connection, int questionId) throws SQLException {
        HashSet<String> answers = new HashSet<>();
        try(PreparedStatement ps = connection.prepareStatement("SELECT option_text FROM "+ getAnswerTableName() +" WHERE question_id = ?")){
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String answerText = rs.getString("option_text");
                answers.add(answerText);
            }
        }
        return answers;
    }

    @Override
    protected void insertAnswersIntoDB(int questionId, Question question, Connection connection) throws SQLException {
        PictureResponse pR = (PictureResponse) question;
        HashSet<String> answers = (HashSet<String>) pR.getCorrectAnswers();
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+ getAnswerTableName() +" (question_id, option_text) VALUES (?, ?)")){
            for(String answer : answers) {
                preparedStatement.setInt(1, questionId);
                preparedStatement.setString(2, answer);
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    protected Question createQuestionObject(int questionId, String questionText, Object answers, int quizId, int orderInQuiz, double maxScore, String imageUrl) {
        return new PictureResponse(questionId,questionText, QuestionType.PICTURE_RESPONSE,quizId, orderInQuiz, (HashSet<String>)answers ,maxScore);
    }

    @Override
    protected String getAnswerTableName() {
        return "AnswerOptionsPR";
    }
}