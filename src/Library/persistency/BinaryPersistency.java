package Library.persistency;

import Library.Archive;

import java.io.*;

/**
 * Write and read collection data to a serialized binary file
 * @author lkoelbel 21487
 */
public class BinaryPersistency implements IPersistency{

    /**
     * Save data
     * @param _collection Collection to save
     * @param _path Path of the file
     * @throws PercistencyException in case of error
     */
    @Override
    public void save(Archive _collection, String _path) throws PercistencyException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(_path))) {
            out.writeObject(_collection);
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
    public Archive load(String _path) throws PercistencyException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(_path)))
        {
            Archive c = (Archive) in.readObject();
            c.sort();
            return c;
        }
            catch (FileNotFoundException e) {
            throw new PercistencyException("File not found exception", "The specified file was not found");
        } catch (IOException e) {
            throw new PercistencyException("File not found exception", "An error occurred while trying to read from the file: " + e.getMessage());
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new PercistencyException("Incorrect data", "The data in the file could not be read. The file could be corrupted or outdated.");
        }
    }
}
