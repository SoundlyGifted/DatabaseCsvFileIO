package web.UploadFileToDBTest.exceptions;

/**
 * Custom genearal application exception class.
 * 
 * @author SoundlyGifted
 */
public class GeneralApplicationException extends Exception {
    
    public GeneralApplicationException() { }
    
    public GeneralApplicationException(String message) {
        super(message);
    }
    
    public GeneralApplicationException(String message, Throwable e) {
        super(message, e);
    }
}
