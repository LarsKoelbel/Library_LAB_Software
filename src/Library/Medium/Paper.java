package Library.Medium;

import Library.bib_tex.BibTexException;
import Library.bib_tex.BibTexParameter;
import Library.bib_tex.BibTexStruct;
import Library.bib_tex.BibTexType;
import Library.io.Communication;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;

/**
 * This class is used to represent a paper (newspaper) storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class Paper extends Medium{
    private String ISSN = null;
    private int volume = 0;
    private int number = 0;
    // New
    private String edition = null;
    private int pages = -1;

    public Paper()
    {
        super.setType(BibTexType.JOURNAL);
    }

    public String getISSN() {
        return ISSN;
    }

    public Paper setISSN(String _ISSN) {
        this.ISSN = _ISSN;
        return this;
    }

    public int getVolume() {
        return volume;
    }

    public Paper setVolume(int _volume) {
        this.volume = _volume;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public Paper setNumber(int _number) {
        this.number = _number;
        return this;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String _edition) {
        this.edition = _edition;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int _pages) {
        this.pages = _pages;
    }

    /**
     * Methode is used to get a string representation of the storage medium
     * @return String representation
     */
    @Override
    public String generateRepresentation()
    {
        StringBuilder sp = new StringBuilder();
        sp.append("Inventory ID: ").append(getInventoryID()).append("\n");
        sp.append("Title: ").append(getTitle()).append("\n");
        sp.append("Status: ").append(getStatus()).append("\n");
        sp.append("Date of Return: ").append(getDateOfReturn() != null ? getDateOfReturn() : "N/A").append("\n");
        sp.append("ISSN: ").append(getISSN()).append("\n");
        sp.append("Volume: ").append(getVolume()).append("\n");
        sp.append("Number: ").append(getNumber()).append("\n");
        sp.append("Edition: ").append(getEdition()).append("\n");
        sp.append("Pages: ").append(getPages());
        return sp.toString();
    }

    /**
     * Parse values from a BibTex struct object
     * @param _bibTexStruct BibTex struct to parse from
     * @param _out Output buffer for the process
     */
    public Paper parseFromBibTexStruct(BibTexStruct _bibTexStruct, ProcessOutputBuffer _out)
    {
        for (BibTexParameter bibTexParameter : _bibTexStruct.getParameterList())
        {
            switch (bibTexParameter.getName().toLowerCase().strip()){
                case "title" -> setTitle(bibTexParameter.getSvalue());
                case "issn" -> setISSN(bibTexParameter.getSvalue());
                case "volume" -> setVolume((int) bibTexParameter.getFvalue());
                case "number" -> setNumber((int) bibTexParameter.getFvalue());
                case "edition" -> setEdition(bibTexParameter.getSvalue());
                case "pages" -> setPages((int) bibTexParameter.getFvalue());
                default -> _out.write("Parameter " + bibTexParameter.getName() + " is not available for type Journal", Severity.WARNING);

            }
        }

        // Check if all the parameters are met
        if (this.getTitle() == null || this.getTitle().isEmpty())
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'title' is missing or empty.");

        if (this.ISSN == null || this.ISSN.isEmpty())
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'issn' is missing or empty.");

        if (this.volume <= 0)
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'volume' is missing or invalid.");

        if (this.number <= 0)
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'number' is missing or invalid.");

        if (this.pages < 0)
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'pages' is missing or empty.");

        if (this.edition == null || this.edition.isEmpty())
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'edition' is missing or empty.");


        return this;
    }
}
