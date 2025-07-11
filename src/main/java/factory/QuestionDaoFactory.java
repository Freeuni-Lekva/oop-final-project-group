package factory;

import dao.AbstractQuestionDao;
import dao.FillInTheBlankDao;
import dao.MultipleChoiceDao;
import dao.MultipleChoiceWithMultipleAnswersDao;

/**
 * Factory class for creating appropriate DAO instances based on question type.
 * Implements the Factory pattern to provide coupling between question types and their DAOs.
 * This allows for easy extension when new question types are added to the system.
 */
public class QuestionDaoFactory {
    /**
     * Creates and returns the appropriate DAO instance for the given question type.
     * @param questionType the database value of the question type (e.g., "MULTIPLE_CHOICE", "FILL_IN_BLANK")
     * @return the corresponding AbstractQuestionDao implementation
     * @throws IllegalArgumentException if the question type is not supported
     */
    public static AbstractQuestionDao getDao(String questionType) {
        switch(questionType) {
            case "MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS": return new MultipleChoiceWithMultipleAnswersDao();
            case "FILL_IN_BLANK": return new FillInTheBlankDao();
            case "MULTIPLE_CHOICE": return new MultipleChoiceDao();
//            case "QUESTION_RESPONSE": return new QuestionResponseDao();
//            case "PICTURE_RESPONSE": return new PictureResponseDao();
            default: throw new IllegalArgumentException("Unknown question type: " + questionType);
        }
    }
}
