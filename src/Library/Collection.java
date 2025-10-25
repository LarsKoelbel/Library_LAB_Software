package Library;

import Library.Medium.Medium;
import Library.Medium.Status;
import Library.bib_tex.BibTexException;
import Library.bib_tex.BibTexParser;
import Library.io.Communication;
import Library.io.IExceptionUserReadable;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.utils.DuplicateEntryException;

import java.io.Serializable;
import java.util.*;

/**
 * Main class for the management of all the entries in the library
 * @author lkoebel 21487
 */
public class Collection implements Iterable<Medium>, Serializable {
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
            libList.add(m);
            SORTED = false;
            sort();
            return true;


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
     * Find a mediums by title
     * @param _title Title
     * @param _out Process output buffer
     * @param _reverse Reverse the list
     * @return The medium or null
     */
    public Medium[] findMedium(String _title, ProcessOutputBuffer _out, boolean _reverse)
    {
        ArrayList<Medium> mediumList = new ArrayList<>();

        for (Medium m : libList)
        {
            if (m.getTitle().equalsIgnoreCase(_title)) mediumList.add(m);
        }

        if (mediumList.isEmpty())
        {
            _out.write("Title could not be found", Severity.ERROR);

            return null;
        }

        mediumList.sort(Comparator.comparing(Medium::getTitle).thenComparingInt(m -> m.getType().getSortLevel()));

        if (_reverse) Collections.reverse(mediumList);

        return mediumList.toArray(new Medium[0]);
    }

    /**
     * Find a mediums by id
     * @param _id ID if the medium
     * @param _out Process output buffer
     * @return The medium or null
     */
    public Medium findMedium(long _id, ProcessOutputBuffer _out)
    {

        for (Medium m : libList)
        {
            if (m.getInventoryID() == _id) return m;
        }

        _out.write("ID could not be found", Severity.ERROR);

        return null;
    }

    /**
     * Find a mediums by title
     * @param _title Title
     * @param _out Process output buffer
     * @return The medium or null
     */
    public Medium[] findMedium(String _title, ProcessOutputBuffer _out)
    {
        return findMedium(_title, _out, false);
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
     * Remove a medium from the library
     * @param _title Title of the medium
     * @param _out Process output buffer
     * @return Success status
     */
    public boolean dropMedium(String _title, ProcessOutputBuffer _out)
    {
        return dropMedium(_title, _out, false);
    }

    /**
     * Remove a medium from the library
     * @param _title Title of the medium
     * @param _out Process output buffer
     * @param _dropAll If multiple are found, drop all
     * @return Success status
     */
    public boolean dropMedium(String _title, ProcessOutputBuffer _out, boolean _dropAll)
    {
        Medium[] m = findMedium(_title, Communication.NULL_BUFFER);
        if (m != null)
        {
            if (m.length > 1)
            {
                if(_dropAll)
                {
                    for (Medium me : m) libList.remove(me);
                }
                else throw new DuplicateEntryException("Multiple titles for delete", Arrays.toString(m));
            }else
            {
                libList.remove(m[0]);
                _out.write("Removed medium of title: " + m[0].getTitle());
                SORTED = false;
                return true;
            }
        }
        _out.write("Medium not found: " + _title, Severity.ERROR);
        return false;
    }

    /**
     * Remove a medium from the library
     * @param _id ID of the medium
     * @param _out Process output buffer
     * @return Success status
     */
    public boolean dropMedium(long _id, ProcessOutputBuffer _out)
    {
        Medium m = findMedium(_id, Communication.NULL_BUFFER);
        if (m != null)
        {
            libList.remove(m);
        }
        _out.write("Medium not found: " + _id, Severity.ERROR);
        return false;
    }

    /**
     * Sort the libList
     */
    public void sort()
    {
        sort(false);
    }

    /**
     * Sort the libList
     * @param _reverse Reverse the list
     */
    public void sort(boolean _reverse)
    {
        if (!SORTED) {
            libList.sort(Medium::compareTo);
            SORTED = true;
        }
        if (_reverse) Collections.reverse(libList);
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
