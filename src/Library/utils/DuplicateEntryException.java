package Library.utils;

import Library.io.IExceptionUserReadable;

/**
* Exception for handling duplicate entry's
* @author lkoeble 21487
*/
public class DuplicateEntryException extends RuntimeException implements IExceptionUserReadable {
    String userMessage = null;
    public DuplicateEntryException(String message, String _userMessage) {
        super(message);
        userMessage = _userMessage;
    }

    @Override
    public String getUserMessage() {
        return userMessage;
    }
}
