package Library;

import Library.Medium.Medium;
import Library.bib_tex.BibTexParser;
import Library.io.*;

/**
 * Main class for managing the library
 * @author lkoelbel
 * @matnr 21487
 */
public class Library {
    public static void main(String[] argv)
    {
        Communication.registerGlobalOutputBufferListener(new IGlobalOutputBufferListener(){

            @Override
            public void onGlobalOutputBufferUpdate(Message _lastMessage) {
                System.out.println(_lastMessage.getSeverity() + ": " + _lastMessage.toString());
            }

            @Override
            public void onProcessOutputBufferClose(ProcessOutputBuffer _processOutputBuffer, String _key) {

                System.out.println("-------- Process end for " + _key + " --------");
                Message mostSevere = _processOutputBuffer.getMostSevere();
                if (mostSevere.getSeverity().getLevel() > Severity.BASIC.getLevel()){
                    System.out.println("Process exited with severity " + mostSevere.getSeverity() + ": ");
                    System.out.println(mostSevere);
                }else
                {
                    System.out.println(_processOutputBuffer);
                }
            }
        });


        Communication.acquireProcessOutputBuffer("bib-tex-parser");
        try
        {
            Medium medium = BibTexParser.parseFromBibTexString("@book{author = {-}, title = {Duden 01. Die deutsche Rechtschreibung}, publisher = {Bibliogr" +
                    "aphisches Institut, Mannheim}, year = 2004, isbn = {3-411-04013-0}}");
            Communication.writeToProcessOutputBuffer("bib-tex-parser",
                    "Result: \n" + medium.generateRepresentation());
        }catch (Exception e)
        {
           if (e instanceof IExceptionUserReadable)
           {
               Communication.writeToProcessOutputBuffer("bib-tex-parser",((IExceptionUserReadable) e).getUserMessage(), Severity.ERROR);
           }else
           {
               Communication.writeToProcessOutputBuffer("bib-tex-parser", e.toString(), Severity.FATAL);
           }
        }

        Communication.processOutputBufferClose("bib-tex-parser");
    }

}
