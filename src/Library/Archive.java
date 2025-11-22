package Library;

import Library.Medium.*;
import Library.bib_tex.BibTexParser;
import Library.bib_tex.BibTexType;
import Library.database.DatabaseException;
import Library.io.Communication;
import Library.io.IExceptionUserReadable;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.utils.DuplicateEntryException;
import Library.utils.ISBNUtils;
import Library.utils.URLUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Main class for the management of all the entries in the library
 * @author lkoebel 21487
 */
public class Archive implements Iterable<Medium>, Serializable {
    private final ArrayList<Medium> libList = new ArrayList<>();
    private boolean SORTED = false;

    public int length()
    {
        return libList.size();
    }

    public boolean isEmpty()
    {
        return length() <= 0;
    }

    /**
     * Get the next available id for the medium
     * @param _out Process output buffer
     * @return ID
     */
    private long getNextID(ProcessOutputBuffer _out)
    {
        // Request an ID from the server if it is connected, else find the best id in the local dataset
        if (Library.server != null)
        {
            if(!Library.server.testAuth(Communication.NULL_BUFFER))
            {
                _out.write("The current session was connected to a server, but the server connection was lost. Database integrity does not allow " +
                        "adding new mediums in unclear operational status. Either reconnect to the server or disconnect officially to proceed.", Severity.WARNING);
                return -1;
            }else
            {
                return Library.server.getFreeId(_out);
            }
        }else
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
            long inventoryID = getNextID(_out);
            if (inventoryID <= 0) return false;
            Medium m = BibTexParser.parseFromBibTexString(_bibTex, _out);
            m.setInventoryID(inventoryID);
            m.setStatus(Status.AVAILABLE);

            // Sync with server
            if (Library.server != null)
            {
                if (!Library.server.testAuth(Communication.NULL_BUFFER))
                {
                    _out.write("The current session was connected to a server, but the server connection was lost. Database integrity does not allow " +
                            "adding new mediums in unclear operational status. Either reconnect to the server or disconnect officially to proceed.", Severity.WARNING);
                    return false;
                }else
                {
                    if(Library.server.add(m, _out))
                    {
                        _out.write("Medium added to server database", Severity.SUCCESS);
                    }
                    else
                    {
                        return false;
                    }
                }
            }

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
     * Add a medium to the collection !!For internal use only. Else use the bibtex interface!!
     * @param medium Medium
     */
    public void addMedium(Medium medium, ProcessOutputBuffer _out)
    {
        medium.setInventoryID(getNextID(_out));
        this.libList.add(medium);
    }

    private void addMedium(Medium medium)
    {
        this.libList.add(medium);
    }

    /**
     * Find a mediums by title
     * @param _title Title
     * @param _out Process output buffer
     * @param _reverse Reverse the list
     * @param _exact Search for exact title
     * @param _ignoreCase Ignore case in exact mode
     * @return The medium or null
     */
    public Medium[] findMedium(String _title, ProcessOutputBuffer _out, boolean _reverse, boolean _exact, boolean _ignoreCase)
    {
        ArrayList<Medium> mediumList = new ArrayList<>();

        for (Medium m : libList)
        {
            if (!_exact)
            {
                if (m.getTitle().toLowerCase().contains(_title.toLowerCase())) mediumList.add(m);
            }
            else
            {
                if (_ignoreCase)
                {
                    if (m.getTitle().equalsIgnoreCase(_title)) mediumList.add(m);
                }
                else
                {
                    if (m.getTitle().equals(_title)) mediumList.add(m);
                }
            }
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
     * Find a mediums by title
     * @param _title Title
     * @param _out Process output buffer
     * @param _reverse Reverse the list
     * @return The medium or null
     */
    public Medium[] findMedium(String _title, ProcessOutputBuffer _out, boolean _reverse)
    {
        return findMedium(_title, _out, _reverse, false, false);
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
                    for (Medium me : m){
                        if (Library.server != null)
                        {
                            if (!Library.server.testAuth(Communication.NULL_BUFFER))
                            {
                                _out.write("The current session was connected to a server, but the server connection was lost. Database integrity does not allow " +
                                        "dropping mediums in unclear operational status. Either reconnect to the server or disconnect officially to proceed.", Severity.WARNING);
                                return false;
                            }else
                            {
                                if(Library.server.delete(me, _out))
                                {
                                    _out.write("Medium dropped from server database: " + me.getInventoryID(), Severity.SUCCESS);
                                }
                                else
                                {
                                    return false;
                                }
                            }
                        }

                        libList.remove(me);
                        _out.write("Removed medium of id: " + me.getInventoryID(), Severity.SUCCESS);
                    }
                    return true;
                }
                else throw new DuplicateEntryException("Multiple titles for delete", Arrays.toString(m));
            }else
            {
                if (Library.server != null)
                {
                    if (!Library.server.testAuth(Communication.NULL_BUFFER))
                    {
                        _out.write("The current session was connected to a server, but the server connection was lost. Database integrity does not allow " +
                                "dropping mediums in unclear operational status. Either reconnect to the server or disconnect officially to proceed.", Severity.WARNING);
                        return false;
                    }else
                    {
                        if(Library.server.delete(m[0], _out))
                        {
                            _out.write("Medium dropped from server database: " + m[0].getInventoryID(), Severity.SUCCESS);
                        }
                        else
                        {
                            return false;
                        }
                    }
                }

                libList.remove(m[0]);
                _out.write("Removed medium of title: " + m[0].getTitle(), Severity.SUCCESS);
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
            if (Library.server != null)
            {
                if (!Library.server.testAuth(Communication.NULL_BUFFER))
                {
                    _out.write("The current session was connected to a server, but the server connection was lost. Database integrity does not allow " +
                            "dropping mediums in unclear operational status. Either reconnect to the server or disconnect officially to proceed.", Severity.WARNING);
                    return false;
                }else
                {
                    if(Library.server.delete(m, _out))
                    {
                        _out.write("Medium dropped from server database: " + m.getInventoryID(), Severity.SUCCESS);
                    }else
                    {
                        return false;
                    }
                }
            }

            libList.remove(m);
            _out.write("Removed medium of id: " + _id, Severity.SUCCESS);
            return true;
        }
        _out.write("Medium of this id not found: " + _id, Severity.ERROR);
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
     * Merge another collection into this one
     * @param other Other collection
     */
    public void merge(Archive other)
    {
        if (other.isEmpty()) return;
        for(Medium m : other)
        {
            libList.add(m);
        }
        SORTED = false;
        sort();
    }

    /**
     * Clear the collection completely
     */
    public void clear()
    {
        libList.clear();
    }

    /**
     * Get a database string from a medium
     * @param medium Medium
     * @return The database string
     */
    public static String getDataBaseString(Medium medium)
    {
        String id = "NULL";
        String title = "NULL";
        String status = "NULL";
        String dateOfReturn = "NULL"; // might be "NULL"
        String type = "NULL";
        String yearOfPublishing = "NULL"; // can be NULL
        String publisher = "NULL";
        String isbn = "NULL";
        String author = "NULL";
        String label = "NULL";
        String artist = "NULL";
        String durationInMinutes = "NULL"; // can be NULL
        String agePolicy = "NULL";
        String url = "NULL";
        String dataFormat = "NULL";
        String sizeInBytes = "NULL";
        String issn = "NULL";
        String volume = "NULL";
        String number = "NULL";
        String edition = "NULL";
        String pages = "NULL";

        id = String.valueOf(medium.getInventoryID());
        title = medium.getTitle();

        switch (medium.getStatus())
        {
            case Status.AVAILABLE -> status = "1";
            case Status.CHECKED_OUT -> status = "0";
            default -> status = "-1";
        }

        if(medium.getDateOfReturn() != null) dateOfReturn = medium.getDateOfReturn().toString();

        if (medium instanceof Book)
        {
            Book m = (Book) medium;

            yearOfPublishing = String.valueOf(m.getYearOfPublishing());
            publisher = m.getPublicher();
            isbn = m.getISBN();
            author = m.getWriter();
            edition = m.getEdition();
            pages = String.valueOf(m.getPages());
            type = "BOOK";
        }
        else if (medium instanceof CD)
        {
            CD m = (CD) medium;

            label = m.getLable();
            artist = m.getArtist();
            durationInMinutes = String.valueOf(m.getDurationInMinutes());
            agePolicy = m.getAgePolicy();
            type = "CD";
        }
        else if (medium instanceof Paper)
        {
            Paper m = (Paper) medium;

            issn = m.getISSN();
            volume = String.valueOf(m.getVolume());
            number = String.valueOf(m.getNumber());
            edition = m.getEdition();
            pages = String.valueOf(m.getPages());
            type = "JOURNAL";
        }
        else if (medium instanceof ElectronicalMedium)
        {
            ElectronicalMedium m = (ElectronicalMedium) medium;

            url = m.getURL();
            dataFormat = m.getDataFormat();
            sizeInBytes = String.valueOf(m.getSizeInBytes());
            type = "EL_MED";
        }

        // Build the database string

        return String.join("<element>",
                id, title, status, dateOfReturn, type,
                yearOfPublishing, publisher, isbn, author,
                label, artist, durationInMinutes, agePolicy,
                url, dataFormat, sizeInBytes, issn, volume,
                number, edition, pages
        );
    }

    /**
     * Generate a new collection from a database string
     * @param _str The database string
     * @return The collection or null
     */
    public static Archive fromDataBaseString(String _str)
    {

        Archive collection = new Archive();

        try
        {
            String[] mediums_data = _str.split("\n");

            for (String med_string : mediums_data)
            {
                String[] data = med_string.split("<element>");

                // Extract all data
                int id = Integer.parseInt(data[0].trim());
                String title = data[1].trim();
                Status status = null;

                switch (Integer.parseInt(data[2].trim())){
                    case 1 -> status = Status.AVAILABLE;
                    case 0 -> status = Status.CHECKED_OUT;
                    default -> status = Status.UNKNOWN;
                }

                String dateOfReturn = data[3].trim(); // might be "NULL"
                BibTexType type = null;

                switch (data[4].trim())
                {
                    case "BOOK" -> type = BibTexType.BOOK;
                    case "CD" -> type = BibTexType.CD;
                    case "JOURNAL" -> type = BibTexType.JOURNAL;
                    case "EL_MED" -> type = BibTexType.EL_MED;
                }

                String yearOfPublishing = data[5].trim(); // can be NULL
                String publisher = data[6].trim();
                String isbn = data[7].trim();
                String author = data[8].trim();
                String label = data[9].trim();
                String artist = data[10].trim();
                String durationInMinutes = data[11].trim(); // can be NULL
                String agePolicy = data[12].trim();
                String url = data[13].trim();
                String dataFormat = data[14].trim();
                String sizeInBytes = data[15].trim();
                String issn = data[16].trim();
                String volume = data[17].trim();
                String number = data[18].trim();
                String edition = data[19].trim();
                String pages = data[20].trim();

                // Create the medium

                Medium medium = null;

                switch (type)
                {
                    case BibTexType.BOOK:
                    {
                        medium = new Book()
                                .setYearOfPublishing(Integer.parseInt(yearOfPublishing))
                                .setPublicher(publisher)
                                .setISBN(isbn)
                                .setWriter(author)
                                .setEdition(edition)
                                .setPages(Integer.parseInt(pages));
                        break;
                    }
                    case BibTexType.CD:
                    {
                        medium = new CD()
                                .setLable(label)
                                .setArtist(artist)
                                .setDurationInMinutes(Double.parseDouble(durationInMinutes))
                                .setAgePolicy(agePolicy);
                        break;
                    }
                    case BibTexType.JOURNAL:
                    {
                        medium = new Paper()
                                .setISSN(issn)
                                .setVolume(Integer.parseInt(volume))
                                .setNumber(Integer.parseInt(number))
                                .setEdition(edition)
                                .setPages(Integer.parseInt(pages));
                        break;
                    }
                    case BibTexType.EL_MED:
                    {
                        medium = new ElectronicalMedium()
                                .setURL(url)
                                .setDataFormat(dataFormat)
                                .setSizeInBytes(Long.parseLong(sizeInBytes));
                    }
                }

                if (medium == null) throw new DatabaseException("Medium type invalid", "The data received from the server is incompatible with the library data format. Please contact administrator!");

                medium.setInventoryID(id)
                        .setTitle(title)
                        .setStatus(status);
                if (dateOfReturn.equalsIgnoreCase("none"))
                {
                    medium.setDateOfReturn(null);
                }else
                {
                    medium.setDateOfReturn(LocalDate.parse(dateOfReturn));
                }

                collection.addMedium(medium);

            }
        } catch (ISBNUtils.ISBNException e) {
            throw new RuntimeException(e);
        } catch (URLUtils.URLException e) {
            throw new RuntimeException(e);
        }

        collection.sort();

        return collection;
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
