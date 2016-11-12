package dao;

import db.AwareExecutor;
import exceptions.GenericDAOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey on 12.11.2016.
 */
public abstract class AbstractDAO<T> {

    protected static AwareExecutor awareExecutor = new AwareExecutor();

    public abstract List<T> findAll() throws GenericDAOException;

    public abstract Optional<? extends T> findById(Long id) throws GenericDAOException ;

    public abstract Optional<? extends T> findByField(Object login) throws GenericDAOException ;

    public abstract Long updateById(Long id, T entity) throws GenericDAOException ;

    public abstract Long insert(T entity) throws GenericDAOException ;

    public abstract Long deleteById(Long id) throws GenericDAOException ;


}
