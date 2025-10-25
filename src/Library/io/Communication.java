package Library.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling communication with the user
 * @author lkoelbel 21487
 */
public class Communication {
    private static final List<IGlobalOutputBufferListener> GLOBAL_OUTPUT_BUFFER_LISTENERS = new ArrayList<>();

    public static final DiscardProcessOutputBuffer NULL_BUFFER = new DiscardProcessOutputBuffer();

    /**
     * Add a listener to the global output buffer
     * @param _globalOutputBufferListener Listener
     */
    public static void registerGlobalOutputBufferListener(IGlobalOutputBufferListener _globalOutputBufferListener)
    {
        GLOBAL_OUTPUT_BUFFER_LISTENERS.add(_globalOutputBufferListener);
    }

    /**
     * Remove a listener from the global output buffer
     * @param _globalOutputBufferListener Listener
     */
    public static void unregisterGlobalOutputBufferListener(IGlobalOutputBufferListener _globalOutputBufferListener)
    {
        GLOBAL_OUTPUT_BUFFER_LISTENERS.remove(_globalOutputBufferListener);
    }

    /**
     * Write a new message to the global output buffer
     * @param _message Message to write (Object.toString() called automatically)
     * @param _severity Severity of the message
     */
    public static void writeToGlobalOutputBuffer(Object _message, Severity _severity)
    {
        Message message = new Message(_message.toString(), _severity);

        for (IGlobalOutputBufferListener globalOutputBufferListener : GLOBAL_OUTPUT_BUFFER_LISTENERS)
        {
            globalOutputBufferListener.onGlobalOutputBufferUpdate(message);
        }
    }

    /**
     * Write a new message to the global output buffer with default severity
     * @param _message Message to write (Object.toString() called automatically)
     */
    public static void writeToGlobalOutputBuffer(Object _message)
    {
        writeToGlobalOutputBuffer(_message, Severity.BASIC);
    }

}
