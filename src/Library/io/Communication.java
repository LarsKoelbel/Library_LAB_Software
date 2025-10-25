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
    private static final Map<String, ProcessOutputBuffer> GLOBAL_PROCESS_OUTPUT_MAP = new HashMap<>();

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

    /**
     * Acquire new process output buffer with key
     * @param _key Key to be used for the buffer
     * @return Key or null if key already exists
     */
    public static String acquireProcessOutputBuffer(String _key)
    {
        if (GLOBAL_PROCESS_OUTPUT_MAP.containsKey(_key)) return null;
        ProcessOutputBuffer processOutputBuffer = new ProcessOutputBuffer();
        GLOBAL_PROCESS_OUTPUT_MAP.put(_key, processOutputBuffer);
        return _key;
    }

    /**
     * Close a process output buffer and notify the listeners
     * @param _key Key of the buffer
     * @return True if key exists, false otherwise
     */
    public static boolean processOutputBufferClose(String _key)
    {
        if (!GLOBAL_PROCESS_OUTPUT_MAP.containsKey(_key)) return false;

        ProcessOutputBuffer processOutputBuffer = GLOBAL_PROCESS_OUTPUT_MAP.get(_key);
        GLOBAL_PROCESS_OUTPUT_MAP.remove(_key);

        for (IGlobalOutputBufferListener globalOutputBufferListener : GLOBAL_OUTPUT_BUFFER_LISTENERS)
        {
            globalOutputBufferListener.onProcessOutputBufferClose(processOutputBuffer, _key);
        }

        return true;
    }

    /**
     * Write a new message to the ga process output buffer
     * @param _message Message to write (Object.toString() called automatically)
     * @param _key Key of the buffer
     * @param _severity Severity of the message
     */
    public static void writeToProcessOutputBuffer(String _key, Object _message, Severity _severity)
    {
        if (!GLOBAL_PROCESS_OUTPUT_MAP.containsKey(_key)) return;

        Message message = new Message(_message.toString(), _severity);

        GLOBAL_PROCESS_OUTPUT_MAP.get(_key).addMessage(message);
    }

    /**
     * Write a new message to a process output buffer with default severity
     * @param _message Message to write (Object.toString() called automatically)
     * @param _key Key of the buffer
     */
    public static void writeToProcessOutputBuffer(String _key, Object _message)
    {
        writeToProcessOutputBuffer(_key,_message, Severity.BASIC);
    }

}
