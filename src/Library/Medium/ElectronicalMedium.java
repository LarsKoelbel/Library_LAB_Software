package Library.Medium;

import Library.bib_tex.*;
import Library.io.ProcessOutputBuffer;
import Library.utils.URLUtils;
import Library.io.Communication;
import Library.io.Severity;

import java.io.Serializable;

/**
 * This class is used to represent an electronic storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class ElectronicalMedium extends Medium implements Serializable {
    private String url = null;
    // New
    private String dataFormat = null;
    private long sizeInBytes = -1;

    public ElectronicalMedium()
    {
        super.setType(BibTexType.EL_MED);
    }

    public String getURL() {
        return url;
    }

    /**
     * Set the url and check if it is valid
     * @param _url URL
     * @throws Library.utils.URLUtils.URLException if the URL is not valide
     * @return The object itself (chained calls)
     */
    public ElectronicalMedium setURL(String _url) throws URLUtils.URLException {
        if(URLUtils.checkURLString(_url))
        {
            this.url = _url;
        }
        else
        {
            throw new URLUtils.URLException(String.format("The URL %s is not valide.", _url));
        }

        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public ElectronicalMedium setDataFormat(String _dataFormat) {
        this.dataFormat = _dataFormat;
        return this;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public ElectronicalMedium setSizeInBytes(long _sizeInBytes) {
        this.sizeInBytes = _sizeInBytes;
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
        sp.append("URL: ").append(getURL()).append("\n");
        sp.append("Data Format: ").append(getDataFormat()).append("\n");
        sp.append("Size (bytes): ").append(getSizeInBytes());
        return sp.toString();
    }

    /**
     * Get a bibtex struct of the object
     * @return the BibTexStruct
     */
    @Override
    public BibTexStruct getBibtex() {
        BibTexStruct bibTex = new BibTexStruct();
        bibTex.init().setType(BibTexType.EL_MED)
                .addParameter(new BibTexParameter()
                        .setName("Title")
                        .setSvalue(getTitle())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("URL")
                        .setSvalue(getURL())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("DataFormat")
                        .setSvalue(getDataFormat())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Size")
                        .setFvalue((double) getSizeInBytes())
                        .setType(BibTexParameterType.NUMERIC_VALUE));

        return bibTex;
    }


    /**
     * Parse values from a BibTex struct object
     * @param _bibTexStruct BibTex struct to parse from
     * @param _out Output buffer for the process
     */
    public ElectronicalMedium parseFromBibTexStruct(BibTexStruct _bibTexStruct, ProcessOutputBuffer _out)
    {
        try
        {
            for (BibTexParameter bibTexParameter : _bibTexStruct.getParameterList())
            {
                switch (bibTexParameter.getName().toLowerCase().strip()){
                    case "title" -> setTitle(bibTexParameter.getSvalue());
                    case "url" -> setURL(bibTexParameter.getSvalue());
                    case "dataformat" -> setDataFormat(bibTexParameter.getSvalue());
                    case "size" -> setSizeInBytes((long) bibTexParameter.getFvalue());
                    default -> _out.write("Parameter " + bibTexParameter.getName() + " is not available for type electronic medium", Severity.WARNING);

                }
            }

            // Check if all the parameters are met
            if (this.getTitle() == null || this.getTitle().isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'title' is missing or empty.");

            if (this.url == null || this.url.isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'url' is missing or empty.");

            if (this.dataFormat == null || this.dataFormat.isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'dataformat' is missing or empty.");

            if (this.sizeInBytes < 0)
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'size' is missing or empty.");

            return this;
        }catch (URLUtils.URLException e)
        {
            throw new BibTexException("URL exception", "Medium can not be created. " + e.getMessage());
        }

    }
}
