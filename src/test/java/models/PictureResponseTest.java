package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PictureResponseTest {

    private PictureResponse pictureResponse;

    @BeforeEach
    void setUp() {
        HashSet<String> allowedAnswers = new HashSet<>();
        allowedAnswers.add("George Washington");
        allowedAnswers.add("Washington");
        allowedAnswers.add("President George Washington");
        allowedAnswers.add("President Washington");
        allowedAnswers.add("The first president of the united states");
        pictureResponse = new PictureResponse( "Who is shown on this image", QuestionType.PICTURE_RESPONSE, 1, 1, "/image1", allowedAnswers, 2);
    }

    @Test
    public void getterAndSetterTest(){
        assertEquals( -1, pictureResponse.getQuestionId());
        pictureResponse.setQuestionId(1);
        assertEquals( 1, pictureResponse.getQuestionId());

        assertEquals( "Who is shown on this image", pictureResponse.getQuestionText());

        assertEquals( "PICTURE_RESPONSE", pictureResponse.getQuestionType());

        assertEquals(1, pictureResponse.getOrderInQuiz());
        pictureResponse.setOrderInQuiz(2);
        assertEquals(2, pictureResponse.getOrderInQuiz());

        assertEquals("/image1", pictureResponse.getImageUrl());
        pictureResponse.setImageUrl("/image2");
        assertEquals("/image2", pictureResponse.getImageUrl());

        assertEquals( 1, pictureResponse.getQuizId());
    }


    @Test
    public void testEquals(){
        HashSet<String> allowedAnswers1 = new HashSet<>();
        allowedAnswers1.add("George Washington");
        allowedAnswers1.add("Washington");
        allowedAnswers1.add("President George Washington");
        allowedAnswers1.add("President Washington");
        allowedAnswers1.add("The first president of the united states");

        PictureResponse test = new PictureResponse( "Who is shown on this image", QuestionType.PICTURE_RESPONSE, 1, 1, "/image1", allowedAnswers1, 2);
        //test when they are equal
        assertTrue(pictureResponse.equals(test));

        test.setQuestionId(1);
        //test when the difference is at the first field
        assertFalse(pictureResponse.equals(test));


        HashSet<String> allowedAnswers2 = new HashSet<>();
        //test when there is a difference in the last field
        PictureResponse test2 = new PictureResponse(1, "Who is shown on this image", QuestionType.PICTURE_RESPONSE, 1, 1, "/image1", allowedAnswers1, 1);
        assertFalse(pictureResponse.equals(test2));

        // test when null
        assertFalse(pictureResponse.equals(null));

        //test when the object is a different type
        assertFalse(pictureResponse.equals(""));
    }

    //test single right answer
    @Test
    public void checkAnswersTest1(){
        List<String> answers = new ArrayList<>();
        answers.add("George Washington");
        List<Boolean> answerBooleans = pictureResponse.checkAnswers(answers);
        for (Boolean answerBoolean : answerBooleans) {
            assertTrue(answerBoolean);
        }
    }

    //test multiple right answers
    @Test
    public void checkAnswersTest2(){
        List<String> answers = new ArrayList<>();
        answers.add("Washington");
        answers.add("President Washington");
        answers.add("The first president of the united states");
        List<Boolean> answerBooleans = pictureResponse.checkAnswers(answers);
        for (Boolean answerBoolean : answerBooleans) {
            assertTrue(answerBoolean);
        }
    }

    //test wrong answers
    @Test
    public void checkAnswersTest3(){
        List<String> answers = new ArrayList<>();
        answers.add("George Washingto");
        answers.add("");
        answers.add("Michael");
        List<Boolean> answerBooleans = pictureResponse.checkAnswers(answers);
        for (Boolean answerBoolean : answerBooleans) {
            assertFalse(answerBoolean);
        }
    }

    //check correctAnswers method
    @Test
    public void getCorrectAnswersTest(){
        HashSet<String> answers = new HashSet<>();
        answers.add("George Washington");
        answers.add("Washington");
        answers.add("President George Washington");
        answers.add("President Washington");
        answers.add("The first president of the united states");
        assertEquals(answers, pictureResponse.getCorrectAnswers());
    }

    //check calculate score for right answers
    @Test
    public void calculateScoreTest1(){
        List<String> answers = new ArrayList<>();
        answers.add("George Washington");
        assertEquals(2.0, pictureResponse.calculateScore(answers));

        answers.add("Washington");
        answers.add("President George Washington");
        answers.add("President Washington");
        answers.add("The first president of the united states");
        assertEquals(2.0, pictureResponse.calculateScore(answers));
    }


    @Test
    public void calculateScoreTest2(){
        List<String> answers = new ArrayList<>();
        answers.add("George Washingto");
        assertEquals(0.0, pictureResponse.calculateScore(answers));

        answers.add("Washington");
        answers.add("President George Washington");
        answers.add("President Washington");
        answers.add("The first president of the united states");
        assertEquals(0.0, pictureResponse.calculateScore(answers));
    }

}