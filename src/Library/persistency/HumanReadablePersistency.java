package Library.persistency;

import Library.Archive;
import Library.Medium.Medium;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Class for writing human-readable data to a file
 * @author lkolbel 21487
 */
public class HumanReadablePersistency implements IPersistency{
    /**
     * Save the representation
     * @param _collection Collection to save
     * @param _path Path of the file
     * @throws PercistencyException in case of errors
     */
    @Override
    public void save(Archive _collection, String _path) throws PercistencyException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(_path), StandardCharsets.UTF_8)))
        {
            StringBuilder sb = new StringBuilder();

            sb.append("---- Short ----\n");

            for (Medium m : _collection)
            {
                sb.append(m.generateShortRepresentation()).append("\n");
            }

            sb.append("---- Long ----\n");

            for (Medium m : _collection)
            {
                sb.append(m.generateRepresentation()).append("\n\n");
            }

            out.write(sb.toString());

        } catch (FileNotFoundException e) {
            throw new PercistencyException("File not found exception", "The specified file was not found and/ or could not be created");
        } catch (IOException e) {
            throw new PercistencyException("File not found exception", "An error occurred while trying to write to the file: " + e.getMessage());
        }
    }

    /**
     * Not implemented
     */
    @Override
    public Archive load(String _path) throws PercistencyException {
        throw new UnsupportedOperationException("Load is not implemented for human-readable format");
    }
}
