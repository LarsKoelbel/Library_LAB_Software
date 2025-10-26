package Library.database;

import Library.io.IExceptionUserReadable;

/**
* Exception for handling database errors
* @author lkoeble 21487
*/
public class DatabaseException extends RuntimeException implements IExceptionUserReadable {
    private String userMessage = null;
    public DatabaseException(String message, String _userMessage) {
        super(message);
        userMessage = _userMessage;
    }

    @Override
    public String getUserMessage() {
        return userMessage;
    }
}
