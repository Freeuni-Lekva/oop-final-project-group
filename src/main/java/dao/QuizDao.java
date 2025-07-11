package dao;

import models.QuizDisplayType;
import models.Quiz;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Quiz entities.
 * Handles database operations for quiz management including creation, retrieval,
 * updating, and deletion of quizzes. Provides methods for quiz validation,
 * creator verification, and question counting. Supports filtering by creation date
 * and creator, enabling features like recent quiz listings and user-specific
 * quiz management.
 */
public class QuizDao {

    /**
     * Adds the Quiz to the database
     * @param quiz the quiz given by the user
     * @return returns true if quiz was added successfully. Returns false otherwise.
     */
    public boolean addQuiz(Quiz quiz){
        try(Connection connection = DatabaseConnection.getConnection()){
            String query = "INSERT INTO Quizzes (creator_user_id, title, description, creation_date," +
                    "is_random_order, display_type, is_immediate_correction, is_practice_mode_enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                setQuizParameters(preparedStatement, quiz);
                preparedStatement.executeUpdate();
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    int quizId = 0;
                    if (resultSet.next()) {
                        quizId = resultSet.getInt(1);
                        quiz.setQuizId(quizId);
                    }else{
                        throw new SQLException("Failed to get generated quiz ID");
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Setting all the quiz parameters except quiz id to the preparedStatement values
    private void setQuizParameters(PreparedStatement preparedStatement, Quiz quiz) throws SQLException {
        preparedStatement.setInt(1, quiz.getCreatorUserId());
        preparedStatement.setString(2, quiz.getTitle());
        preparedStatement.setString(3, quiz.getDescription());
        preparedStatement.setObject(4, quiz.getCreationDate());
        preparedStatement.setBoolean(5, quiz.isRandomOrder());
        preparedStatement.setString(6, quiz.getQuizDisplayType().getDatabaseValue());
        preparedStatement.setBoolean(7, quiz.isImmediateCorrection());
        preparedStatement.setBoolean(8, quiz.isPracticeModeEnabled());
    }

    /**
     * Returns the quiz with the given id from the database
     * @param quizId the id of a quiz to return
     * @return the Quiz that has the given id
     */
    public Quiz getQuizById(int quizId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Quizzes WHERE quiz_id = ? ")){
            preparedStatement.setInt(1, quizId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                   Quiz quiz = getQuizFromResultSet(resultSet, quizId);
                   // Now, fetch the questions for this quiz
                   List<models.Question> questions = new ArrayList<>();
                   String questionsQuery = "SELECT question_id, question_type FROM Questions WHERE quiz_id = ? ORDER BY order_in_quiz";
                   try (PreparedStatement questionsStmt = connection.prepareStatement(questionsQuery)) {
                       questionsStmt.setInt(1, quizId);
                       try (ResultSet questionsRs = questionsStmt.executeQuery()) {
                           while (questionsRs.next()) {
                               int questionId = questionsRs.getInt("question_id");
                               String questionTypeStr = questionsRs.getString("question_type");
                               AbstractQuestionDao questionDao = factory.QuestionDaoFactory.getDao(questionTypeStr);
                               questions.add(questionDao.getQuestionById(questionId));
                           }
                       }
                   }
                   quiz.setQuestions(questions);
                   return quiz;
               }
           }
           return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Changes the quiz to a new quiz. The quiz_id remains the same.
     * @param quiz A new quiz containing quiz_id of an old question
     * @return returns boolean indicating if the operation was successful or not
     */
    public boolean updateQuiz(Quiz quiz){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Quizzes SET creator_user_id = ?, title = ?, description = ?, " +
                            "creation_date = ?, is_random_order = ?, display_type = ?," +
                            "is_immediate_correction = ?, is_practice_mode_enabled = ? WHERE quiz_id = ?")){
            setQuizParameters(preparedStatement, quiz);
            preparedStatement.setInt(9, quiz.getQuizId());
            int rows = preparedStatement.executeUpdate();
            return rows != 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the quiz with the given quiz id
     * @param quizId the id of a quiz to delete
     * @return returns boolean indicating if the operation was successful or not
     */
    public boolean deleteQuiz(int quizId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Quizzes WHERE quiz_id = ?")){
            preparedStatement.setInt(1, quizId);
            int rows = preparedStatement.executeUpdate();
            return rows != 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns all the quizzes created by the user
     * @param creatorUserId the id of a user
     * @return the list of quizzes created by a user
     */
    public List<Quiz> getAllQuizzesByCreator(int creatorUserId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Quizzes WHERE creator_user_id = ?")){
            preparedStatement.setInt(1, creatorUserId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return getQuizzes(resultSet);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //helper method, gets all the parameters from resultset, constructs a quiz with them and returns it
    private List<Quiz> getQuizzes(ResultSet resultSet) throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        while (resultSet.next()) {
            int quizId = resultSet.getInt("quiz_id");
            Quiz quiz = getQuizFromResultSet(resultSet, quizId);
            quizzes.add(quiz);
        }
        return quizzes;
    }

    /**
     * @return returns the list containing all the quizzes in the database
     */
    public List<Quiz> getAllQuizzes(){
        try(Connection connection = DatabaseConnection.getConnection()){
            Statement statement = connection.createStatement();
            try(ResultSet resultSet = statement.executeQuery("SELECT * FROM Quizzes")){
                return getQuizzes(resultSet);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //helper method, gets all the parameters from resultset, constructs a quiz with them and returns it
    private Quiz getQuizFromResultSet(ResultSet resultSet, int quizId) throws SQLException {
        int creatorUserId = resultSet.getInt("creator_user_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        Timestamp timestamp = resultSet.getTimestamp("creation_date");
        LocalDateTime creationDate = timestamp.toLocalDateTime();
        boolean randomOrder = resultSet.getBoolean("is_random_order");
        QuizDisplayType quizDisplayType = QuizDisplayType.valueOf(resultSet.getString("display_type"));
        boolean immediateCorrection = resultSet.getBoolean("is_immediate_correction");
        boolean practiceModeEnabled = resultSet.getBoolean("is_practice_mode_enabled");
        return new Quiz(quizId, creatorUserId, title, description, creationDate, randomOrder, quizDisplayType,
                immediateCorrection, practiceModeEnabled);
    }

    /**
     * Returns the list of recently created quizzes up to the limit specified by the user
     * @param limit the maximum limit of quizzes to return
     * @return the list of recently created quizzes
     */
    public List<Quiz> getRecentlyCreatedQuizzes(int limit){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Quizzes ORDER BY creation_date DESC LIMIT ?")){
            preparedStatement.setInt(1, limit);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return getQuizzes(resultSet);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param quizId the id of a quiz
     * @return boolean indicating if quiz with given id exists or not
     */
    public boolean quizExists(int quizId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM Quizzes WHERE quiz_id = ?")) {
            preparedStatement.setInt(1, quizId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }catch (SQLException e){
            return false;
        }
    }

    /**
     * @param quizId the id of a quiz
     * @param userId the id of a user
     * @return boolean indicating if the user with a given id is a creator of a quiz with a given id
     */
    public boolean isQuizCreator(int quizId, int userId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT creator_user_id FROM Quizzes WHERE quiz_id = ?")) {
            preparedStatement.setInt(1, quizId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return userId == resultSet.getInt("creator_user_id");
                }
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param quizId the id of a quiz
     * @return returns the number of questions are in a quiz
     */
    public int getQuestionCount(int quizId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM Questions WHERE quiz_id = ?")) {
            preparedStatement.setInt(1, quizId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
}
