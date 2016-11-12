package dao;

import db.AwareExecutor;
import exceptions.GenericDAOException;
import ray.Coordinates;

import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey on 12.11.2016.
 */
public class CoordinatesDAO extends AbstractDAO<Coordinates> {
    @Override
    public List<Coordinates> findAll() throws GenericDAOException {
        return new AwareExecutor().submit(session -> (List<Coordinates>) session.createQuery("FROM Coordinates").list());
    }

    @Override
    public Optional<? extends Coordinates> findById(Long id) throws GenericDAOException {
        return null;
    }

    @Override
    public Optional<? extends Coordinates> findByField(Object login) throws GenericDAOException {
        return null;
    }

    @Override
    public Long updateById(Long id, Coordinates entity) throws GenericDAOException {
        return null;
    }

    @Override
    public Long insert(Coordinates entity) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            session.save(entity);
            return entity.getId();
        });
    }

    @Override
    public Long deleteById(Long id) throws GenericDAOException {
        return null;
    }
}
