package Library.Medium;

import Library.bib_tex.*;
import Library.io.Communication;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;

import java.io.Serializable;

/**
 * This class is used to represent a paper (newspaper) storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class Paper extends Medium implements Serializable {
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

    public Paper setEdition(String _edition) {
        this.edition = _edition;
        return this;
    }

    public int getPages() {
        return pages;
    }

    public Paper setPages(int _pages) {
        this.pages = _pages;
        return this;
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
        sp.append("Type: ").append(getType()).append("\n");
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
     * Get a bibtex struct of the object
     * @return the BibTexStruct
     */
    @Override
    public BibTexStruct getBibtex() {
        BibTexStruct bibTex = new BibTexStruct();
        bibTex.init().setType(BibTexType.JOURNAL)
                .addParameter(new BibTexParameter()
                        .setName("Title")
                        .setSvalue(getTitle())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("ISSN")
                        .setSvalue(getISSN())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Volume")
                        .setFvalue((double) getVolume())
                        .setType(BibTexParameterType.NUMERIC_VALUE))
                .addParameter(new BibTexParameter()
                        .setName("Number")
                        .setFvalue((double) getNumber())
                        .setType(BibTexParameterType.NUMERIC_VALUE))
                .addParameter(new BibTexParameter()
                        .setName("Edition")
                        .setSvalue(getEdition())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Pages")
                        .setFvalue((double) getPages())
                        .setType(BibTexParameterType.NUMERIC_VALUE));

        return bibTex;
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
