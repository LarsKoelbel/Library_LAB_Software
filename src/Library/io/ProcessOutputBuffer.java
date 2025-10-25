package Library.io;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an output buffer for a process
 * @author lkoelbel 21487
 */
public class ProcessOutputBuffer {
    private List<Message> buffer = new ArrayList<>();

    /**
     * Add a message to the buffer
     * @param _message Message to be added
     */
    public void addMessage(Message _message)
    {
        this.buffer.add(_message);
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
        sb.append("######################################\n");
        for (Message m : this.buffer)
        {
            sb.append(m.getSeverity()).append(": \n").append(m).append("\n");
        }
        sb.append("######################################\n");

        return sb.toString();
    }
}
