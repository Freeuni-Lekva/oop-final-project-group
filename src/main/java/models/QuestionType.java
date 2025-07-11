package models;

/**
 * Enumeration of supported question types in the quiz system.
 * Each type corresponds to a specific database value and question implementation.
 */
public enum QuestionType {
    FILL_IN_BLANK("FILL_IN_BLANK"),
    MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
    PICTURE_RESPONSE("PICTURE_RESPONSE"),
    QUESTION_RESPONSE("QUESTION_RESPONSE"),
    MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS("MULTIPLE_CHOICE_WITH_MULTIPLE_ANSWERS");


    private final String databaseValue;

    QuestionType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    /**
     * Returns the database representation of this question type.
     * @return the string value stored in the database
     */
    public String getDatabaseValue() {
        return databaseValue;
    }
}
