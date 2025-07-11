package factory;

import dao.AbstractQuestionDao;
import dao.FillInTheBlankDao;
import dao.MultipleChoiceDao;
import dao.MultipleChoiceWithMultipleAnswersDao;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionDaoFactoryTest {

    //Tests if passing MULTIPLE_CHOICE returns the correct class
    @Test
    public void testGetMultipleChoice() {
        AbstractQuestionDao dao = QuestionDaoFactory.getDao("MULTIPLE_CHOICE");
        assertInstanceOf(MultipleChoiceDao.class, dao);
    }

    //Tests if passing FILL_IN_BLANK returns the correct class
    @Test
    public void testGetFillInTheBlank() {
        AbstractQuestionDao dao = QuestionDaoFactory.getDao("FILL_IN_BLANK");
        assertInstanceOf(FillInTheBlankDao.class, dao);
    }

    //Tests if passing MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS returns the correct class
    @Test
    public void testGetMultipleChoiceWithMultipleAnswers() {
        AbstractQuestionDao dao = QuestionDaoFactory.getDao("MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS");
        assertInstanceOf(MultipleChoiceWithMultipleAnswersDao.class, dao);
    }

    //Tests if passing Question_Response returns the correct class
//    @Test
//    public void testGetQuestionResponse() {
//        AbstractQuestionDao dao = QuestionDaoFactory.getDao("QUESTION_RESPONSE");
//        assertInstanceOf(QuestionResponseDao.class, dao);
//    }
//
    //Tests if passing Question_Response returns the correct class
//    @Test
//    public void testGetPictureResponse() {
//        AbstractQuestionDao dao = QuestionDaoFactory.getDao("QUESTION_RESPONSE");
//        assertInstanceOf(QuestionResponseDao.class, dao);
//    }

    //Tests if passing wrong question type throws the correct error
    @Test
    public void testWrongQuestionType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> QuestionDaoFactory.getDao("fill_in_the_blank")
        );
        assertEquals("Unknown question type: fill_in_the_blank", exception.getMessage());

        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> QuestionDaoFactory.getDao("multiple_choice")
        );
        assertEquals("Unknown question type: multiple_choice", exception2.getMessage());

        IllegalArgumentException exception3 = assertThrows(
                IllegalArgumentException.class,
                () -> QuestionDaoFactory.getDao("")
        );
        assertEquals("Unknown question type: ", exception3.getMessage());
    }
}
