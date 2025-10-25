package Library.bib_tex;

import Library.io.IExceptionUserReadable;

/**
 * Exception for runtime errors inside the bibtex module
 * @author lkoelbel 21487
 */
public class BibTexException extends RuntimeException implements IExceptionUserReadable {
  private String userMessage = null;

  public BibTexException(String _message, String _userMessage) {
    super(_message);
    this.userMessage = _userMessage;
  }

  @Override
  public String getUserMessage() {
    return userMessage;
  }

  /**
   * Get a string with an arrow showing the location of an error is a string
   * @param _input The string in question
   * @param _index The index the arrow should be placed at
   * @return The complete string with the arrow
   */
  public static String getTexPositionMarkerForError(String _input, int _index)
  {
      StringBuilder sb = new StringBuilder("\n");
      sb.append(_input).append("\n");
      sb.append(String.valueOf(' ').repeat(_index)).append("^").append("\n");

      return sb.toString();
  }
}
