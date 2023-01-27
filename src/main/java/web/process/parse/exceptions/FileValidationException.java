package web.process.parse.exceptions;

/**
 * Custom genearal application exception class.
 * 
 * @author SoundlyGifted
 */
public class FileValidationException extends Exception {
    
    public FileValidationException() { }
    
    public FileValidationException(String message) {
        super(message);
    }
    
    public FileValidationException(String message, Throwable e) {
        super(message, e);
    }
}
