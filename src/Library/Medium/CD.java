package Library.Medium;

import Library.bib_tex.BibTexParameter;
import Library.bib_tex.BibTexStruct;
import Library.io.Communication;
import Library.io.Severity;

/**
 * This class is used to represent a CD storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class CD extends Medium{
    private String lable = null;
    private String artist = null;

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

    /**
     * Methode is used to get a string representation of the storage medium
     * @return String representation
     */
    @Override
    public String generateRepresentation()
    {
        StringBuilder sp = new StringBuilder();
        sp.append("Title: ").append(getTitle()).append("\n");
        sp.append("Lable: ").append(getLable()).append("\n");
        sp.append("Artist: ").append(getArtist());

        return sp.toString();
    }

    /**
     * Parse values from a BibTex struct object
     * @param _bibTexStruct BibTex struct to parse from
     */
    public CD parseFromBibTexStruct(BibTexStruct _bibTexStruct)
    {
        for (BibTexParameter bibTexParameter : _bibTexStruct.getParameterList())
        {
            switch (bibTexParameter.getName().toLowerCase().strip()){
                case "title" -> setTitle(bibTexParameter.getSvalue());
                case "label" -> setLable(bibTexParameter.getSvalue());
                case "artist" -> setArtist(bibTexParameter.getSvalue());
                default -> Communication.writeToProcessOutputBuffer("bib-tex-parser","Parameter " + bibTexParameter.getName() + " is not available for type CD", Severity.WARNING);

            }
        }

        return this;
    }
}
