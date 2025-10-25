package Library.io;

/**
 * Enum for the severity of a message
 * @author lkoelbel 21487
 */
public enum Severity {
    BASIC(0),
    WARNING(1),
    ERROR(2),
    FATAL(3);

    private int level;
    private static final int HIGHEST_SEVERITY = 3;

    /**
     * Construct to set a level (comparable)
     * @param _level Level of the severety
     */
    Severity(int _level)
    {
        this.level = _level;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Get the highest possible severity level
     * @return Highest severity level
     */
    public static int highestSeverity()
    {
        return HIGHEST_SEVERITY;
    }
}
