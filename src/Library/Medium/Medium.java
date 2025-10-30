package Library.Medium;

import Library.Library;
import Library.bib_tex.BibTexStruct;
import Library.bib_tex.BibTexType;
import Library.database.IServerAddress;
import Library.io.Communication;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;

import java.io.Serializable;
import java.security.cert.TrustAnchor;
import java.time.LocalDate;

/**
 * This class is used as a template for all storage mediums
 * @author lkoelbel
 * @matnr 21487
 */
abstract public class Medium implements Comparable<Medium>, Serializable {
    private String title = null;
    private long inventoryID = -1;
    private Status status = null;
    private LocalDate dateOfReturn = null;
    private BibTexType type = BibTexType.MEDIUM;

    public String getTitle() {
        return title;
    }

    public Medium setTitle(String _title) {
        this.title = _title;
        return this;
    }

    public long getInventoryID() {
        return inventoryID;
    }

    public Medium setInventoryID(long _inventoryID) {
        this.inventoryID = _inventoryID;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Medium setStatus(Status _status) {
        this.status = _status;
        return this;
    }

    public LocalDate getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(LocalDate dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    public BibTexType getType() {
        return type;
    }

    public void setType(BibTexType type) {
        this.type = type;
    }

    /**
     * Generate a short representation of the Medium for compact list views
     * @return Short representation
     */
    public String generateShortRepresentation()
    {
        StringBuilder sp = new StringBuilder();
        sp.append("Inventory ID: ").append(getInventoryID()).append(" | ")
                .append("Title: ").append(getTitle()).append(" | ")
                .append("Type: ").append(getType()).append(" | ")
                .append("Status: ").append(getStatus()).append(" | ")
                .append("Date of return: ").append(getDateOfReturn() != null ? getDateOfReturn() : "N/A").append(" | ");
        return sp.toString();
    }

    /**
     * Check out a medium if it is available
     * @param _dateOfReturn Date when the book has to be returned
     * @param _out Process output buffer
     * @return Success state (True if success)
     */
    public boolean checkOut(LocalDate _dateOfReturn, ProcessOutputBuffer _out)
    {
        // Database integrity, check for server availability
        if(Library.server != null)
        {
            if (Library.server.testAuth(Communication.NULL_BUFFER))
            {
                if(!Library.server.modStatus(this, _out, Status.CHECKED_OUT)) return false;
            }
            else
            {
                _out.write("The current session was connected to a server, but the server connection was lost. Database integrity does not allow " +
                        "changing medium state in unclear operational status. Either reconnect to the server or disconnect officially to proceed.", Severity.WARNING);
                return false;
            }
        }

        if (status == Status.AVAILABLE)
        {
            status = Status.CHECKED_OUT;
            dateOfReturn = _dateOfReturn;
            return true;
        }

        return false;
    }

    /**
     * Return a medium
     * @param _out Process output buffer
     */
    public boolean giveBack(ProcessOutputBuffer _out)
    {
        // Database integrity, check for server availability
        if(Library.server != null)
        {
            if (Library.server.testAuth(Communication.NULL_BUFFER))
            {
                if(!Library.server.modStatus(this, _out, Status.AVAILABLE)) return false;
            }
            else
            {
                _out.write("The current session was connected to a server, but the server connection was lost. Database integrity does not allow " +
                        "changing medium state in unclear operational status. Either reconnect to the server or disconnect officially to proceed.", Severity.WARNING);
                return false;
            }
        }
        status = Status.AVAILABLE;
        dateOfReturn = null;

        return true;
    }

    /**
     * Extend the loan period of a medium if possible
     * @param _nawDateOfReturn New return date
     * @return Success state (True if success)
     */
    public boolean extendLoanPeriod(LocalDate _nawDateOfReturn)
    {
        if (status == Status.CHECKED_OUT)
        {
            dateOfReturn = _nawDateOfReturn;
            return true;
        }

        return false;
    }

    /**
     * Methode is used to get a string representation of the storage medium
     * @return String representation
     */
    abstract public String generateRepresentation();

    /**
     * Get a bibtex struct of the object
     * @return the BibTexStruct
     */
    abstract public BibTexStruct getBibtex();

    /**
     * Comparing to mediums means sorting them alphabetically
     * @param o Other
     * @return Return of compare to
     */
    @Override
    public int compareTo(Medium o) {
        return title.compareTo(o.title);
    }

    /**
     * Get the short representation
     * @return Short representation
     */
    @Override
    public String toString()
    {
        return generateShortRepresentation();
    }
}
