package dao;

import models.PictureResponse;
import models.Question;
import models.QuestionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PictureResponseDaoTest {
    private DBConnection dbConnection;
    private PictureResponseDao pictureResponseDao;
    private int testQuizId;
    private PictureResponse pR1;
    private PictureResponse pR2;

    @BeforeEach
    void setUp() throws SQLException {
        dbConnection = new DBConnection();
        //DBCreate dbCreate = new DBCreate();
//        try {
//            dbCreate.createDataBase(dbConnection);
//        } catch (IOException | SQLException e) {
//            throw new RuntimeException(e);
//        }
        dbConnection.getConnection().prepareStatement(
                "INSERT IGNORE INTO Users (username, email, password_hash, salt) " +
                        "VALUES ('testuser', 'test@example.com', 'testhash', 'testsalt')"
        ).executeUpdate();

        PreparedStatement stmt = dbConnection.getConnection().prepareStatement(
                "INSERT INTO Quizzes (creator_user_id, title) " +
                        "SELECT user_id, 'Test Quiz' FROM Users WHERE username = 'testuser'",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        testQuizId = keys.getInt(1);
        keys.close();
        stmt.close();


        pictureResponseDao = new PictureResponseDao();
    }

    @AfterEach
    public void cleanUp() throws SQLException {
        PreparedStatement stmt = dbConnection.getConnection().prepareStatement("DELETE FROM Quizzes WHERE quiz_id = ?");
        stmt.setInt(1, testQuizId);
        stmt.executeUpdate();
        stmt.close();
        dbConnection.getConnection().close();
    }

    @Test
    void addAndGetTest(){
        loadQuestions();
        assertTrue(pictureResponseDao.addQuestion(pR1));
        assertTrue(pictureResponseDao.addQuestion(pR2));
        int questionId1 = pR1.getQuestionId();
        int questionId2 = pR2.getQuestionId();


        PictureResponse pR1Get = (PictureResponse) pictureResponseDao.getQuestionById(questionId1);
        PictureResponse pR2Get = (PictureResponse) pictureResponseDao.getQuestionById(questionId2);

        assertTrue(pR1Get.equals(pR1));
        assertTrue(pR2Get.equals(pR2));
    }


    @Test
    void getAllTest(){
        loadQuestions();
        assertTrue(pictureResponseDao.addQuestion(pR1));
        assertTrue(pictureResponseDao.addQuestion(pR2));

        List<Question> questions = pictureResponseDao.getAllQuestions(testQuizId);
        assertTrue(questions.size() == 2);
        assertTrue(questions.get(0).equals(pR1));
        assertTrue(questions.get(1).equals(pR2));
    }


    @Test
    void updateTest(){
        String questionText1 = "Who is this?";
        HashSet<String> allowedAnswers1 = new HashSet<>();
        allowedAnswers1.add("George Washington");
        allowedAnswers1.add("Washington");
        allowedAnswers1.add("President George Washington");
        allowedAnswers1.add("President Washington");
        allowedAnswers1.add("The first president of the united states");
        PictureResponse pR1 = new PictureResponse( questionText1, QuestionType.PICTURE_RESPONSE, testQuizId, 0, "GeorgeWashington.png", allowedAnswers1);
        assertTrue(pictureResponseDao.addQuestion(pR1));
        int questionId1 = pR1.getQuestionId();
        String questionText2 = "Who is this?";
        HashSet<String> allowedAnswers2 = new HashSet<>();
        allowedAnswers2.add("dog");
        allowedAnswers2.add("a dog");
        PictureResponse pR2 = new PictureResponse(pR1.getQuestionId(), questionText2, QuestionType.PICTURE_RESPONSE, testQuizId, 1, "GeorgeWashington.png", allowedAnswers2);
        assertTrue(pictureResponseDao.updateQuestion(pR2));

        PictureResponse pR1Get = (PictureResponse) pictureResponseDao.getQuestionById(questionId1);
        assertTrue(pR1Get.equals(pR2));
    }

    @Test
    void deleteQuestionTest(){
        loadQuestions();
        assertTrue(pictureResponseDao.addQuestion(pR1));
        assertTrue(pictureResponseDao.addQuestion(pR2));
        int questionId1 = pR1.getQuestionId();
        int questionId2 = pR2.getQuestionId();
        assertTrue(pictureResponseDao.deleteQuestion(questionId2));
        assertNull(pictureResponseDao.getQuestionById(questionId2));
        assertTrue(pictureResponseDao.deleteQuestion(questionId1));
        assertNull(pictureResponseDao.getQuestionById(questionId1));
        assertFalse(pictureResponseDao.deleteQuestion(questionId1));
    }

    private void loadQuestions(){
        String questionText1 = "Who is this?";
        HashSet<String> allowedAnswers1 = new HashSet<>();
        allowedAnswers1.add("George Washington");
        allowedAnswers1.add("Washington");
        allowedAnswers1.add("President George Washington");
        allowedAnswers1.add("President Washington");
        allowedAnswers1.add("The first president of the united states");
        pR1 = new PictureResponse( questionText1, QuestionType.PICTURE_RESPONSE, testQuizId, 0, "GeorgeWashington.png", allowedAnswers1);

        String questionText2 = "Who is this?";
        HashSet<String> allowedAnswers2 = new HashSet<>();
        allowedAnswers2.add("dog");
        allowedAnswers2.add("a dog");
        pR2 = new PictureResponse( questionText2, QuestionType.PICTURE_RESPONSE, testQuizId, 1, "GeorgeWashington.png", allowedAnswers2);

    }
}