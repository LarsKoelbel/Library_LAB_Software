package Library.persistency;

import Library.Collection;
import Library.io.ProcessOutputBuffer;

/**
 * Interface for saving and loading collection data
 * @author lkoelbel 21487
 */
public interface IPersistency
{
    /**
     * Saving a collection to a file
     * @param _collection Collection to save
     * @param _path Path of the file
     * @throws PercistencyException in case of errors
     */
    void save(Collection _collection, String _path) throws PercistencyException;

    /**
     * Loading a collection from a file
     * @param _path Path to the file
     * @return Collection#
     * @throws PercistencyException in case of errors
     */
    Collection load(String _path) throws PercistencyException;
}
