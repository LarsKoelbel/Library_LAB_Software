package Library.user_interface;

import Library.io.ProcessOutputBuffer;

/**
* Endpoint for a cli path (usually a function)
* @author lkoeble 21487
*/
public interface ICLIEndpoint {
    /**
     * Function to be called by the CLI
     * @param params Parameters from the cli
     * @param _out Output buffer
     */
    void call(String[] params, ProcessOutputBuffer _out);

    /**
     * Get the name of the running process
     * @return Process name
     */
    String getProcessName();
}
