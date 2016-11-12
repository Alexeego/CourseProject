package db;

import org.hibernate.Session;

/**
 * Created by Alexey on 12.11.2016.
 */
@FunctionalInterface
public interface ConnectionTask<T> {
    T execute(Session session);
}
