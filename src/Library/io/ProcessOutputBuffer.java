package Library.io;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an output buffer for a process
 * @author lkoelbel 21487
 */
public class ProcessOutputBuffer {
    private List<Message> buffer = new ArrayList<>();
    private String name = null;

    public boolean hasMessages()
    {
        return length() > 0;
    }

    public long length()
    {
        return buffer.size();
    }

    /**
     * Constructor for creating a nes Process output buffer
     * @param _processName Name of the process
     */
    public ProcessOutputBuffer(String _processName)
    {
        this.name = _processName;
    }

    /**
     * Add a message to the buffer
     * @param _message Message to be added
     */
    public void addMessage(Message _message)
    {
        this.buffer.add(_message);
    }

    /**
     * Write a new object to the global output buffer
     * @param _object Object to be printed
     * @param _severity Severity of the message
     */
    public void write(Object _object, Severity _severity)
    {
        this.addMessage(new Message(_object.toString(), _severity));
    }

    /**
     * Write a new object to the global output buffer with default severity
     * @param _object Object to be printed
     */
    public void write(Object _object)
    {
        this.write(_object, Severity.BASIC);
    }

    /**
     * Get all messages from the buffer
     * @return All messages from the buffer
     */
    public Message[] getAll(){
        return this.buffer.toArray(Message[]::new);
    }

    /**
     * Get most severe message from the buffer
     * @return Most severe message
     */
    public Message getMostSevere()
    {
        if (this.buffer.isEmpty()) return null;
        Message mostSevere = null;

        for (Message m : this.buffer)
        {
            if (mostSevere == null) {
                mostSevere = m;
                continue;
            };
            if (m.getSeverity().getLevel() > mostSevere.getSeverity().getLevel()) mostSevere = m;
            if (m.getSeverity().getLevel() == Severity.highestSeverity()) break;
        }

        return mostSevere;
    }

    /**
     * Get a complete string representation of the buffer
     * @return String representation
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("##### ").append("Process buffer trace of: ").append(name).append("\n");
        for (Message m : this.buffer)
        {
            sb.append(m.getSeverity()).append(": \n").append(m).append("\n");
        }
        sb.append("# END ################################\n");

        return sb.toString();
    }
}
