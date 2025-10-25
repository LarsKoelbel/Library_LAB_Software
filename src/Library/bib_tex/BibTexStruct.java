package Library.bib_tex;

import Library.io.ProcessOutputBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a bibtex query with a type and parameters
 * @author lkoelbel 21487
 */
public class BibTexStruct {

    private BibTexType type = null;
    private List<BibTexParameter> parameterList = null;

    /**
     * Parse the dta of the bibtex struct from a string
     * @param _input String query to parse
     * @param _out Output buffer for the process
     * @return The struct itself for chained calls
     */
    public BibTexStruct parseFromString(String _input, ProcessOutputBuffer _out)
    {
        int absoluteParsePosition = 0;

        // Check if the string start with an @

        if (!_input.startsWith("@")) throw new BibTexException("Prefix error", "The input " + _input + " is not a valide Bibtex command: Prefix '@' not found"
                + BibTexException.getTexPositionMarkerForError(_input, absoluteParsePosition));

        // Get the type of the struct

        StringBuilder type = new StringBuilder();

        char c  = (char) 0;
        for (int i = 0; i < _input.length(); i++)
        {
            if ((c = _input.charAt(i)) == '{') {
                absoluteParsePosition = i + 1;
                break;
            }
            if (c == '}') throw new BibTexException("Scope error", "Cant parse bibtex type. Most likely a braced was misplaced at index " + i
                    + BibTexException.getTexPositionMarkerForError(_input, absoluteParsePosition));
            type.append(c);
        }

        // Parse type
        String type_name = type.toString().strip().toLowerCase().replace("@","");

        switch (type_name){
            case "book":
                this.type = BibTexType.BOOK;
                break;
            case "journal":
                this.type = BibTexType.JOURNAL;
                break;
            case "cd":
                this.type = BibTexType.CD;
                break;
            case "elmed":
                this.type = BibTexType.EL_MED;
                break;
            default:
                throw new BibTexException("type error", type_name + " is not a valid bibtex type"
                        + BibTexException.getTexPositionMarkerForError(_input, absoluteParsePosition));
        }

        // Extract parameters

        this.parameterList = new ArrayList<>();

        while (absoluteParsePosition < _input.length())
        {
            int level = 0;
            StringBuilder parameterStringBuilder = new StringBuilder();
            for (int i = absoluteParsePosition; i < _input.length(); i++)
            {
                if ((c = _input.charAt(i)) == '{') level++;
                if (c == '}') {
                    if (level > 0) level--;
                    else {
                        absoluteParsePosition = i + 1;
                        break;
                    }
                }
                if (c == ',' && level == 0) {
                    absoluteParsePosition = i + 1;
                    break;
                }

                parameterStringBuilder.append(c);

                if (i >= _input.length() - 1) throw new BibTexException("Parameter delimiter exception", "The query " + _input + " cant be parsed correctly. " +
                        "Most likely a braked or comma was misplaced in the parameter starting at index " + absoluteParsePosition
                + BibTexException.getTexPositionMarkerForError(_input, absoluteParsePosition));
            }

            String[] splitParameter = getStrings(parameterStringBuilder, _input, absoluteParsePosition);

            String name = splitParameter[0];
            String value = splitParameter[1];
            BibTexParameterType parmType = null;

            if (value.contains("{")) parmType = BibTexParameterType.STRING;
            else parmType = BibTexParameterType.NUMERIC_VALUE;

            BibTexParameter parameter = null;

            if (parmType == BibTexParameterType.NUMERIC_VALUE)
            {
                try {
                    double fvalue = Double.valueOf(value.replace("{","").replace("}","").strip());
                    this.parameterList.add(new BibTexParameter().setName(name).setFvalue(fvalue).setType(parmType));
                }catch (NumberFormatException _)
                {
                    throw new BibTexException("Parameter type exception", "The value " + value + " for the parameter " + name + " is not a valide numerical value. Use name = {value} for string parameters."
                            + BibTexException.getTexPositionMarkerForError(_input, absoluteParsePosition));
                }
            }else
            {
                this.parameterList.add(new BibTexParameter().setName(name).setSvalue(value.replace("{","").replace("}","").strip()).setType(parmType));
            }

            if (absoluteParsePosition >= _input.length() - 3) break;
        }

        return this;
    }

    /**
     * Get a string array representing a parameter of the structure: name = value as [name, value]
     * @param parameterStringBuilder String builder containing the parameter string
     * @param _input Complete query (used for error messages)
     * @param _pos Parse position (used for error messages)
     * @return Split string
     */
    private static String[] getStrings(StringBuilder parameterStringBuilder, String _input, int _pos) {
        String parameterString = parameterStringBuilder.toString();

        if(!parameterString.contains("=")) throw new BibTexException("Parameter exception", "The parameter " + parameterString + " has no '=' and therefore cant be parsed"
                + BibTexException.getTexPositionMarkerForError(_input, _pos));
        if(parameterString.indexOf("=") != parameterString.lastIndexOf("=")) throw new BibTexException("Parameter exception", "The parameter " + parameterString + " has more than one '=' and therefore cant be parsed. Most likely a coma is missing after the parameter"
                + BibTexException.getTexPositionMarkerForError(_input, _pos));
        String[] splitParameter = parameterString.split("=");
        return splitParameter;
    }

    /**
     * Generate a string representation of the struct
     * @return String representation of the struct
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (this.type == null) {
            sb.append("BibTexStruct has not jet been initialised");
            return sb.toString();
        }

        sb.append("BibTexStruct of type ")
                .append(this.type.toString())
                .append("\n");
        for (BibTexParameter bibTexParameter : this.parameterList)
        {
            sb.append("-------------------------------")
            .append(bibTexParameter.toString());
        }

        return sb.toString();
    }

    public List<BibTexParameter> getParameterList()
    {
        return this.parameterList;
    }

    public BibTexType getType()
    {
        return this.type;
    }

    /**
     * Init a new struct for manual creation
     * @return Self (chained cals)
     */
    public BibTexStruct init()
    {
        parameterList = new ArrayList<>();
        return this;
    }

    public BibTexStruct setType(BibTexType type) {
        this.type = type;
        return this;
    }

    /**
     * Add a bib tex parameter
     * @param _parameter The parameter
     * @return self
     */
    public BibTexStruct addParameter(BibTexParameter _parameter)
    {
        if (parameterList == null) parameterList = new ArrayList<>();
        parameterList.add(_parameter);
        return this;
    }

    /**
     * Get the BibTex String
     * @return The BibTex String
     */
    public String getBibTexString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("@").append(type.getRegex()).append("{");

        for (BibTexParameter p : parameterList)
        {
            sb.append(p.getName()).append(" = ");
            if (p.getType() == BibTexParameterType.STRING) sb.append("{").append(p.getSvalue()).append("}");
            else sb.append(p.getFvalue());
            sb.append(", ");
        }
        sb.delete(sb.length()-2, sb.length());
        sb.append("}");

        return sb.toString();
    }
}
