package Library.utils;

/**
 * This class is used to handle ISBNs
 * @author lkoelbel
 * @matnr 21487
 */
public class ISBNUtils {

    /**
     * Exception subclass for ISBN errors
     */
    public static class ISBNException extends Exception
    {
        public ISBNException(String message) {
            super(message);
        }
    }

    /**
     * Check if an ISBN is valide
     * @param _isbn ISBN to check
     * @return True if the ISBN is valide, otherwise False
     */
    public static boolean checkISBNString(String _isbn)
    {
        _isbn = _isbn.replace("-","");
        // Check the length of the isbn, must be 10 ot 13
        int length = _isbn.length();
        switch (length)
        {
            case 10:
                try
                {
                    int[] numbers = getIntArrayFromISBNString(_isbn);
                    return checkISBN10(numbers);
                }catch (ISBNException e)
                {
                    return false;
                }
            case 13:
                try
                {
                    int[] numbers = getIntArrayFromISBNString(_isbn);
                    return checkISBN13(numbers);
                }catch (ISBNException e)
                {
                    return false;
                }
            default:
                return false;
        }

    }

    /**
     * Split the ISBN string into an array of integers
     * @param _isbn ISBN to convert
     * @throws ISBNException If the conversion failed
     * @return The split ISBN
     */
    private static int[] getIntArrayFromISBNString(String _isbn) throws ISBNException {
        int[] numbers = new int[_isbn.length()];
        for (int i = 0; i < _isbn.length(); i++)
        {

            Character c = _isbn.charAt(i);
            if (Character.isDigit(c))
            {
                numbers[i] = Character.getNumericValue(c);
            }
            else
            {
                throw new ISBNException(String.format("Failed to convert ISBN %s to int[]. Char at position %d is not a digit.", _isbn, i));
            }

        }

        return numbers;
    }

    private static boolean checkISBN10(int[] isbn) {
        int sum = 0;
        for (int i = 1; i <= isbn.length; i++) {
            sum += i * isbn[i - 1];
        }
        if (sum % 11 == 0) {
            return true;
        } else {

            return false;
        }

    }

    private static boolean checkISBN13(int[] isbn) {
        int sum = 0;
        for (int i = 1; i < isbn.length; i++) {
            if (i % 2 == 0) {
                sum += isbn[i - 1] * 3;
            } else {
                sum += isbn[i - 1];
            }
        }

        int lastDigit = sum % 10;

        int check = (10 - lastDigit) % 10;

        if (isbn[isbn.length - 1] == check) {
            return true;
        } else {
            return false;
        }
    }
}
