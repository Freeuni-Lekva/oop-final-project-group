package dao;

import models.FillInTheBlankQuestion;
import models.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Data access object for fill-in-the-blank questions.
 * Handles database operations specific to questions with multiple text blanks,
 * where each blank can have multiple acceptable answers.
 * Uses the "FillInBlankAnswers" table to store answers with
 * blank_index to maintain the order of blanks in the question.
 */
public class FillInTheBlankDao extends AbstractQuestionDao {
    @Override
    protected Object getAnswersFromDB(Connection connection, int questionId) throws SQLException {
        ArrayList<HashSet<String>> answersList = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM FillInBlankAnswers WHERE question_id = ?")){
            preparedStatement.setInt(1, questionId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int blankIndex = resultSet.getInt("blank_index");
                    String answerText = resultSet.getString("acceptable_answer");
                    while (answersList.size() <= blankIndex) {
                        answersList.add(new HashSet<>());
                    }
                    answersList.get(blankIndex).add(answerText);
                }
            }
        }
        return answersList;
    }

    @Override
    protected void insertAnswersIntoDB(int questionId, Question question, Connection connection) throws SQLException {
        FillInTheBlankQuestion currQuestion = (FillInTheBlankQuestion) question;
        List<HashSet<String>> answersList = currQuestion.getCorrectAnswers();
        String query = "INSERT INTO FillInBlankAnswers (question_id, blank_index, acceptable_answer) VALUES (?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            for(int i = 0; i < answersList.size(); i++) {
                HashSet<String> answers = answersList.get(i);
                for(String answer : answers) {
                    preparedStatement.setInt(1, questionId);
                    preparedStatement.setInt(2, i);
                    preparedStatement.setString(3, answer);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    @Override
    protected Question createQuestionObject(int questionId, String questionText, Object answers, int quizId, int orderInQuiz) {
        return new FillInTheBlankQuestion(questionId, questionText, (ArrayList<HashSet<String>>)answers, quizId, orderInQuiz);
    }

    @Override
    protected String getAnswerTableName() {
        return "FillInBlankAnswers";
    }
}