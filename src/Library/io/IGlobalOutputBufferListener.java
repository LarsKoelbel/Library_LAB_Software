package Library.io;

/**
 * Methods for all classes listening to the global output buffer
 * @author lkoelbel 21487
 */
public interface IGlobalOutputBufferListener {
    /**
     * Is called when the global output buffer updates
     * @param _lastMessage Last message in the buffer
     */
    public void onGlobalOutputBufferUpdate(Message _lastMessage);

    /**
     * Is called when a process output buffer is closed
     * @param _processOutputBuffer The buffer
     * @param _key Key of the buffer
     */
    public void onProcessOutputBufferClose(ProcessOutputBuffer _processOutputBuffer, String _key);
}
