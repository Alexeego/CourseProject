package exceptions;

/**
 * Created by Alexey on 29.10.2016.
 */
public class UniqueDAOException extends GenericDAOException {
    public UniqueDAOException(Exception e) {
        super("The exception occurred in DAO: There was an attempt to add a non-unique element", e);
    }
}
