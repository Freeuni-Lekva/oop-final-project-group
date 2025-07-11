package dao;

import models.MultipleChoiceQuestion;
import models.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Data access object for single-answer multiple choice questions.
 * Handles database operations for questions with predefined options
 * where users select exactly one choice from available options.
 * Enforces that exactly one option is marked as correct.
 * Uses the "AnswerOptionsMC" table to store options with
 * their correctness flags.
 */
public class MultipleChoiceDao extends AbstractQuestionDao {

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
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion)question;
        Map<String, Boolean> options = multipleChoiceQuestion.getOptions();
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
        return new MultipleChoiceQuestion(questionId, questionText, (Map<String, Boolean>)answers, quizId, orderInQuiz, maxScore);
    }

    @Override
    protected String getAnswerTableName() {
        return "AnswerOptionsMC";
    }
}