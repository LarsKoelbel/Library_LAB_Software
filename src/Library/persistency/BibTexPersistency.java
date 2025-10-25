package Library.persistency;

import Library.Collection;
import Library.Medium.Medium;
import Library.io.Communication;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BibTexPersistency implements IPersistency{
    /**
     * Save data
     * @param _collection Collection to save
     * @param _path Path of the file
     * @throws PercistencyException in case of error
     */
    @Override
    public void save(Collection _collection, String _path) throws PercistencyException {
        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(_path), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();

            for (Medium m : _collection)
            {
                sb.append(m.getBibtex().getBibTexString()).append("\n");
            }

            out.write(sb.toString());
        }
        catch (FileNotFoundException e) {
            throw new PercistencyException("File not found exception", "The specified file was not found and/ or could not be created");
        } catch (IOException e) {
            throw new PercistencyException("File not found exception", "An error occurred while trying to write to the file: " + e.getMessage());
        }
    }

    /**
     * Load the collection from a file
     * @param _path Path to the file
     * @return The collection
     * @throws PercistencyException in case of errors
     */
    @Override
    public Collection load(String _path) throws PercistencyException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(_path), StandardCharsets.UTF_8)))
        {
            Collection c = new Collection();

            String line;
            while ((line = in.readLine()) != null)
            {
                if (line.isBlank()) continue;
                if (!c.addMedium(line,Communication.NULL_BUFFER))
                {
                    throw new PercistencyException("BibTex Parse exception", "One ore more BibTex lines are faulty");
                }
            }

            c.sort();
            return c;
        }
        catch (FileNotFoundException e) {
            throw new PercistencyException("File not found exception", "The specified file was not found");
        } catch (IOException e) {
            throw new PercistencyException("File not found exception", "An error occurred while trying to read from the file: " + e.getMessage());
        }
    }
}
