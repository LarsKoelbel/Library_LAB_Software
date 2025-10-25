package Library.Medium;

import Library.utils.URLUtils;
import Library.bib_tex.BibTexParameter;
import Library.bib_tex.BibTexStruct;
import Library.io.Communication;
import Library.io.Severity;

/**
 * This class is used to represent an electronic storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class ElectronicalMedium extends Medium{
    private String url = null;

    public String getURL() {
        return url;
    }

    public ElectronicalMedium setURL(String _url) {
        if(URLUtils.checkURLString(_url))
        {
            this.url = _url;
        }
        else
        {
            System.out.println(String.format("The URL %s is not valide.", _url));
        }

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
        sp.append("URL: ").append(getURL());

        return sp.toString();
    }

    /**
     * Parse values from a BibTex struct object
     * @param _bibTexStruct BibTex struct to parse from
     */
    public ElectronicalMedium parseFromBibTexStruct(BibTexStruct _bibTexStruct)
    {
        for (BibTexParameter bibTexParameter : _bibTexStruct.getParameterList())
        {
            switch (bibTexParameter.getName().toLowerCase().strip()){
                case "title" -> setTitle(bibTexParameter.getSvalue());
                case "url" -> setURL(bibTexParameter.getSvalue());
                default -> Communication.writeToProcessOutputBuffer("bib-tex-parser","Parameter " + bibTexParameter.getName() + " is not available for type Electronic medium", Severity.WARNING);

            }
        }

        return this;
    }
}
