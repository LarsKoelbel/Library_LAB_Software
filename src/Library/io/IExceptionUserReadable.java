package Library.io;

/**
 * Interface for making an exception supply a user readable error message
 * @author lkoelbel 21487
 */
public interface IExceptionUserReadable {
    /**
     * Get a user readable error message
     * @return User readable error message
     */
    public String getUserMessage();
}
