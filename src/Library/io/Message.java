package Library.io;

/**
 * Clas representing a message
 * @author lkoelbel 21487
 */
public class Message {
    private String message;
    private Severity severity;
    private long timestamp;

    /**
     * Constructor for a new message
     * @param _message Message content
     * @param _severity Severity of the message
     */
    public Message(String _message, Severity _severity)
    {
        this.message = _message;
        this.severity = _severity;
        this.timestamp = System.currentTimeMillis();
    }

    public Severity getSeverity() {
        return severity;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * To string methode gets the content of the message
     * @return Message content
     */
    @Override
    public String toString()
    {

        return "---- Message=" + timestamp + "\n" + this.message + "\n-----------------\n";
    }
}
