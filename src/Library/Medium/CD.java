package Library.Medium;

import Library.bib_tex.*;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;

import java.io.Serializable;

/**
 * This class is used to represent a CD storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class CD extends Medium implements Serializable {
    private String lable = null;
    private String artist = null;
    // New
    private double durationInMinutes = -1;
    private String agePolicy = null;

    public CD()
    {
        super.setType(BibTexType.CD);
    }

    public String getLable() {
        return lable;
    }

    public CD setLable(String _lable) {
        this.lable = _lable;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public CD setArtist(String _artist) {
        this.artist = _artist;
        return this;
    }

    public double getDurationInMinutes() {
        return durationInMinutes;
    }

    public CD setDurationInMinutes(double _durationInMinutes) {
        this.durationInMinutes = _durationInMinutes;
        return this;
    }

    public String getAgePolicy() {
        return agePolicy;
    }

    public CD setAgePolicy(String _agePolicy) {
        this.agePolicy = _agePolicy;
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
        sp.append("Label: ").append(getLable()).append("\n");
        sp.append("Artist: ").append(getArtist()).append("\n");
        sp.append("Duration (minutes): ").append(getDurationInMinutes()).append("\n");
        sp.append("Age Policy: ").append(getAgePolicy());
        return sp.toString();
    }

    /**
     * Get a bibtex struct of the object
     * @return the BibTexStruct
     */
    @Override
    public BibTexStruct getBibtex() {
        BibTexStruct bibTex = new BibTexStruct();
        bibTex.init().setType(BibTexType.CD)
                .addParameter(new BibTexParameter()
                        .setName("Title")
                        .setSvalue(getTitle())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Label")
                        .setSvalue(getLable())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Artist")
                        .setSvalue(getArtist())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Duration")
                        .setFvalue(getDurationInMinutes())
                        .setType(BibTexParameterType.NUMERIC_VALUE))
                .addParameter(new BibTexParameter()
                        .setName("AgePolicy")
                        .setSvalue(getAgePolicy())
                        .setType(BibTexParameterType.STRING));

        return bibTex;
    }


    /**
     * Parse values from a BibTex struct object
     * @param _bibTexStruct BibTex struct to parse from
     * @param _out Output buffer for the process
     */
    public CD parseFromBibTexStruct(BibTexStruct _bibTexStruct, ProcessOutputBuffer _out)
    {
        for (BibTexParameter bibTexParameter : _bibTexStruct.getParameterList())
        {
            switch (bibTexParameter.getName().toLowerCase().strip()){
                case "title" -> setTitle(bibTexParameter.getSvalue());
                case "label" -> setLable(bibTexParameter.getSvalue());
                case "artist" -> setArtist(bibTexParameter.getSvalue());
                case "duration" -> setDurationInMinutes(bibTexParameter.getFvalue());
                case "agepolicy" -> setAgePolicy(bibTexParameter.getSvalue());
                default -> _out.write("Parameter " + bibTexParameter.getName() + " is not available for type CD", Severity.WARNING);

            }
        }

        // Check if all the parameters are met
        if (this.getTitle() == null || this.getTitle().isEmpty())
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'title' is missing or empty.");

        if (this.lable == null || this.lable.isEmpty())
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'label' is missing or empty.");

        if (this.artist == null || this.artist.isEmpty())
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'artist' is missing or empty.");

        if (this.durationInMinutes < 0)
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'duration' is missing or empty.");

        if (this.agePolicy == null || this.agePolicy.isEmpty())
            throw new BibTexException("Incomplete parameter exception",
                    "Medium can not be created. The parameter 'agePolicy' is missing or empty.");

        return this;
    }
}
