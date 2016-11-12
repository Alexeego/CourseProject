package exceptions;

/**
 * Created by Alexey on 29.10.2016.
 */
public class GenericDAOException extends Exception {
    public GenericDAOException(Exception e) {
        super("The exception occurred in DAO", e);
    }

    public GenericDAOException(String message, Exception e) {
        super(message, e);
    }
}
