package Library;

import Library.Medium.Medium;
import Library.Medium.Status;
import Library.bib_tex.BibTexException;
import Library.bib_tex.BibTexParser;
import Library.io.Communication;
import Library.io.IExceptionUserReadable;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Main class for the management of all the entries in the library
 * @author lkoebel 21487
 */
public class Collection implements Iterable<Medium>{
    private final ArrayList<Medium> libList = new ArrayList<>();
    private boolean SORTED = false;

    /**
     * Get the next available id for the medium
     * @return ID
     */
    private long getNextID()
    {
        // Get the heist id
        long heigh = 0;
        for (Medium m : libList)
        {
            long id = m.getInventoryID();

            if(id > heigh)
            {
                heigh = id;
            }
        }

        // Create list of all possible ids
        ArrayList<Long> IDList = new ArrayList<>(java.util.stream.LongStream.rangeClosed(1, heigh + 1)
                .boxed()
                .toList());


        // Remove all taken
        for (Medium m : libList)
        {
            long id = m.getInventoryID();
            IDList.remove(id);
        }

        IDList.sort((a,b) -> Math.toIntExact(a-b));

        return IDList.getFirst();
    }

    /**
     * Add a new medium to the library from a bibTex String
     * @param _bibTex BibTex string
     * @param _out Process output buffer
     * @return Success status
     */
    public boolean addMedium(String _bibTex, ProcessOutputBuffer _out)
    {
        try
        {
            Medium m = BibTexParser.parseFromBibTexString(_bibTex, _out);
            m.setInventoryID(getNextID());
            m.setStatus(Status.AVAILABLE);
            if (checkIfTitleFree(m.getTitle()))
            {
                libList.add(m);
                SORTED = false;
                sort();
                return true;
            }
            else
            {
                _out.write("Title is already taken by another medium: " + m.getTitle(), Severity.ERROR);
                return false;
            }


        } catch (Exception e)
        {
            if (e instanceof IExceptionUserReadable)
            {
                _out.write(((IExceptionUserReadable) e).getUserMessage(), Severity.ERROR);
            }else
            {
                _out.write(e, Severity.FATAL);
            }
        }

        return false;
    }

    /**
     * Find a medium by title
     * @param _title Title
     * @param _out Process output buffer
     * @return The medium or null
     */
    public Medium findMedium(String _title, ProcessOutputBuffer _out)
    {
        for (Medium m : libList)
        {
            if (m.getTitle().equalsIgnoreCase(_title)) return m;
        }
        _out.write("Title could not be found", Severity.ERROR);

        return null;
    }

    /**
     * Check if a title already exists
     * @param _title Title
     * @return True of fals
     */
    private boolean checkIfTitleFree(String _title)
    {
        return findMedium(_title, Communication.NULL_BUFFER) == null;
    }

    /**
     * Remove a new medium to the library
     * @param _title Title of the medium
     * @param _out Process output buffer
     * @return Success status
     */
    public boolean dropMedium(String _title, ProcessOutputBuffer _out)
    {
        Medium m = findMedium(_title, Communication.NULL_BUFFER);
        if (m != null)
        {
            libList.remove(m);
            _out.write("Removed medium of title: " + m.getTitle());
            SORTED = false;
            return true;
        }
        _out.write("Medium not found: " + _title, Severity.ERROR);
        return false;
    }

    /**
     * Sort the libList
     */
    public void sort()
    {
        if (!SORTED) {
            libList.sort(Medium::compareTo);
            SORTED = true;
        }
    }

    @Override
    public Iterator<Medium> iterator() {
        return new CollectionIterator();
    }

    /**
     * Iterator class for the collection
     * @author lkoelbel 21487
     */
    private final class CollectionIterator implements Iterator<Medium>
    {

        int index = 0;

        @Override
        public boolean hasNext() {
            return index < libList.size();
        }

        @Override
        public Medium next() {
            return libList.get(index++);
        }
    }
}
