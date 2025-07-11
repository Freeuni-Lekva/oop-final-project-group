package dao;

import models.MultipleChoiceWithMultipleAnswersQuestion;
import models.Question;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Data access object for multiple choice with multiple answer questions.
 * Handles database operations for questions with predefined options
 * where users select from available choices.
 * Uses the "AnswerOptionsMC" table to store options with
 * their correctness flags.
 */

public class MultipleChoiceWithMultipleAnswersDao extends AbstractQuestionDao {

    @Override
    protected Object getAnswersFromDB(Connection connection, int questionId) throws SQLException {
        Map<String, Boolean> options = new HashMap<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AnswerOptionsMC WHERE question_id = ?")){
            preparedStatement.setInt(1, questionId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    String answerText = resultSet.getString("option_text");
                    Boolean isCorrect = resultSet.getBoolean("is_correct");
                    options.put(answerText, isCorrect);
                }
            }
        }
        return options;
    }

    @Override
    protected void insertAnswersIntoDB(int questionId, Question question, Connection connection) throws SQLException {
        MultipleChoiceWithMultipleAnswersQuestion multipleChoiceWithMultipleAnswersQuestion = (MultipleChoiceWithMultipleAnswersQuestion)question;
        Map<String, Boolean> options = multipleChoiceWithMultipleAnswersQuestion.getOptions();
        String query = "INSERT INTO AnswerOptionsMC (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            for(String answer : options.keySet()) {
                preparedStatement.setInt(1, questionId);
                preparedStatement.setString(2, answer);
                preparedStatement.setBoolean(3, options.get(answer));
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    protected Question createQuestionObject(int questionId, String questionText, Object answers, int quizId, int orderInQuiz, double maxScore){
        return new MultipleChoiceWithMultipleAnswersQuestion(questionId, questionText, (Map<String, Boolean>)answers, quizId, orderInQuiz, maxScore);
    }

    @Override
    protected String getAnswerTableName() {
        return "AnswerOptionsMC";
    }
}