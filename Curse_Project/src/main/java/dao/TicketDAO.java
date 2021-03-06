package dao;

import db.AwareExecutor;
import exceptions.GenericDAOException;
import org.hibernate.Query;
import ray.Place;
import ray.Ray;
import ray.StatePlace;
import ticket.Ticket;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexey on 13.11.2016.
 */
public class TicketDAO extends AbstractDAO<Ticket> {
    @Override
    public List<Ticket> findAll() throws GenericDAOException {
        return awareExecutor.submit(session -> (List<Ticket>) session.createQuery("FROM Ticket ").list());
    }

    @Override
    public Optional<? extends Ticket> findById(Long id) throws GenericDAOException {
        return awareExecutor.submit(session -> Optional.ofNullable((Ticket) session.get(Ticket.class, id)));
    }

    @Override
    public Optional<? extends Ticket> findByFields(Object... fields) throws GenericDAOException {
        if (fields.length >= 3 && fields[0] instanceof Long && fields[1] instanceof Integer && fields[2] instanceof String)
            return awareExecutor.submit(session -> Optional.ofNullable((Ticket) session
                    .createQuery("from Ticket where ray=:ray and numberPlace=:numberPlace and userName=:userName")
                    .setLong("ray", (Long) fields[0])
                    .setInteger("numberPlace", (Integer) fields[1])
                    .setString("userName", (String) fields[2])
                    .uniqueResult()));
        return Optional.empty();
    }

    @Override
    public Long updateById(Long id, Ticket entity) throws GenericDAOException {
        if (id != null)
            return awareExecutor.submit(session -> {
                Ticket ticket = (Ticket) session.get(Ticket.class, id);
                if (ticket != null) {
                    ticket.setUserName(entity.getUserName());
                    session.update(ticket);
                }
                return null;
            });
        return null;
    }

    @Override
    public Long insert(Ticket entity) throws GenericDAOException {
        return awareExecutor.submit(session -> {
            session.save(entity);
            return entity.getId();
        });
    }

    @Override
    public Long deleteById(Long id) throws GenericDAOException {
        if (id != null)
            return awareExecutor.submit(session -> {
                Ticket ticket = (Ticket) session.get(Ticket.class, id);
                if (ticket != null)
                    session.delete(ticket);
                return null;
            });
        return null;
    }

    public void deleteAllBooksTicketsInRay(Long id, List<Integer> placeNumbers) {
        if (id != null) {
            awareExecutor.submit(session -> {
                Query query = session.createQuery("from Ticket where ray=:ray").setLong("ray", id);
                for (Ticket ticket : (List<Ticket>) query.list()) {
                    Iterator<Integer> iterator = placeNumbers.iterator();
                    while (iterator.hasNext()) {
                        if (ticket.getNumberPlace() == iterator.next()) {
                            session.delete(ticket);
                            iterator.remove();
                            break;
                        }
                    }
                }
                return null;
            });
        }
    }
}
