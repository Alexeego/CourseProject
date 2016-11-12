package dao;

import db.AwareExecutor;
import exceptions.GenericDAOException;
import org.hibernate.Query;
import user.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey on 12.11.2016.
 */
public class UserDAO extends AbstractDAO<User> {

    @Override
    public List<User> findAll() throws GenericDAOException {
        return new AwareExecutor().submit(session -> (List<User>) session.createQuery("FROM User").list());
    }

    @Override
    public Optional<? extends User> findById(Long id) throws GenericDAOException {
        return awareExecutor.submit(session -> Optional.ofNullable((User) session.get(User.class, id)));
    }

    @Override
    public Optional<? extends User> findByField(Object name) throws GenericDAOException {
        if (name != null)
            return awareExecutor.submit(session -> {
                Query query = session.createQuery("FROM User where name=:name");
                query.setString("name", (String) name);
                List list = query.list();
                if (!list.isEmpty())
                    return Optional.of((User) list.get(0));
                else return Optional.empty();
            });
        return Optional.empty();
    }

    @Override
    public Long updateById(Long id, User entity) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            User user = (User) session.get(User.class, id);
            if(user != null) {
                user.setAccess(entity.getAccess());
                session.update(user);
            }
            return null;
        });
    }

    @Override
    public Long insert(User entity) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            session.save(entity);
            return entity.getId();
        });
    }

    @Override
    public Long deleteById(Long id) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            User user = (User) session.get(User.class, id);
            if (user != null)
                session.delete(user);
            return null;
        });
    }
}
