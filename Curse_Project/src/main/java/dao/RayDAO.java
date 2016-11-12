package dao;

import db.AwareExecutor;
import exceptions.GenericDAOException;
import ray.Ray;

import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey on 12.11.2016.
 */
public class RayDAO extends AbstractDAO<Ray> {
    @Override
    public List<Ray> findAll() throws GenericDAOException {
        return new AwareExecutor().submit(session -> (List<Ray>) session.createQuery("FROM Ray").list());
    }

    @Override
    public Optional<? extends Ray> findById(Long id) throws GenericDAOException {
        return awareExecutor.submit(session -> Optional.ofNullable((Ray) session.get(Ray.class, id)));
    }

    @Override
    public Optional<? extends Ray> findByField(Object login) throws GenericDAOException {
        return null;
    }

    @Override
    public Long updateById(Long id, Ray entity) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            Ray ray = (Ray) session.get(Ray.class, id);
            if(ray != null) {
                ray.copy(entity);
                session.update(ray);
            }
            return null;
        });
    }

    @Override
    public Long insert(Ray entity) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            session.save(entity);
            return entity.getId();
        });
    }

    @Override
    public Long deleteById(Long id) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            Ray ray = (Ray) session.get(Ray.class, id);
            if (ray != null)
                session.delete(ray);
            return null;
        });
    }
}
