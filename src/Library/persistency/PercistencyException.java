package Library.persistency;

import Library.io.IExceptionUserReadable;

/**
 * Exception for persistency operations
 * @author lkoelbel 21487
 */
public class PercistencyException extends RuntimeException implements IExceptionUserReadable {
    private String userMessage = null;

    /**
     * Create a new exception with a message and a user message
     * @param message Message
     * @param _userMessage User message
     */
    public PercistencyException(String message, String _userMessage) {
        super(message);
        userMessage = _userMessage;
    }

    @Override
    public String getUserMessage() {
        return userMessage;
    }
}
