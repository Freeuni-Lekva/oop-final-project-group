package models;

/**
 * Enumeration of supported quiz display types in the quiz system.
 * The quiz is displayed as either single page or multipage.
 */
public enum QuizDisplayType {
    SINGLE_PAGE("SINGLE_PAGE"),
    MULTI_PAGE_QUESTION("MULTI_PAGE_QUESTION");

    private final String databaseValue;

    QuizDisplayType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    /**
     * Returns the database representation of this quiz display type.
     * @return the string value stored in the database
     */
    public String getDatabaseValue() {
        return databaseValue;
    }
}
