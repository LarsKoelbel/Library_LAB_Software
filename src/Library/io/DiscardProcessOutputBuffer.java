package Library.io;

/**
 * Output buffer standin for discarding all output
 * @author lkoebel 21487
 */
public class DiscardProcessOutputBuffer extends ProcessOutputBuffer{
    /**
     * Constructor for creating a new Discarding Process output buffer
     */
    public DiscardProcessOutputBuffer() {
        super("null-buffer");
    }

    /**
     * Empty write methode - Ignores all input
     * @param _object Object to be printed
     * @param _severity Severity of the message
     */
    @Override
    public void write(Object _object, Severity _severity) {

    }

    /**
     * Empty write methode - Ignores all input
     * @param _object Object to be printed
     */
    @Override
    public void write(Object _object) {

    }
}
