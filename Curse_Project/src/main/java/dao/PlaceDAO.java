package dao;

import db.AwareExecutor;
import exceptions.GenericDAOException;
import ray.Place;

import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey on 12.11.2016.
 */
public class PlaceDAO extends AbstractDAO<Place> {
    @Override
    public List<Place> findAll() throws GenericDAOException {
        return new AwareExecutor().submit(session -> (List<Place>) session.createQuery("FROM Place").list());
    }

    @Override
    public Optional<? extends Place> findById(Long id) throws GenericDAOException {
        return null;
    }

    @Override
    public Optional<? extends Place> findByFields(Object... fields) throws GenericDAOException {
        return null;
    }

    @Override
    public Long updateById(Long id, Place entity) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            Place place = (Place) session.get(Place.class, id);
            if(place != null) {
                place.copy(entity);
                session.update(place);
            }
            return null;
        });
    }

    @Override
    public Long insert(Place entity) throws GenericDAOException {
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
