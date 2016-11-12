package connection;

import com.fasterxml.jackson.core.type.TypeReference;
import dao.RayDAO;
import exceptions.GenericDAOException;
import ray.Place;
import ray.Ray;
import ray.StatePlace;
import ticket.Ticket;
import user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static server.Server.*;


/**
 * Created by Alexey on 16.09.2016.
 */
class ConnectionAndroid extends Connection {

    protected ConnectionAndroid(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        super(socket, out, in);
    }

    @Override
    public void send(Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message.getMessageType().toString());
            out.writeObject(message.getData());
            out.flush();
        }
    }

    @Override
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (in) {
            MessageType type = MessageType.valueOf((String) in.readObject());
            String data = (String) in.readObject();
            return new Message(type, data);
        }
    }


    private Set<Ticket> readyBookTickets = new LinkedHashSet<>();
    private User user;
    private RayDAO rayDAO = new RayDAO();

    @Override
    public void serverMainLoop(User user) throws IOException, ClassNotFoundException, GenericDAOException {
        synchronized (lock_rays) {
            send(new Message(MessageType.RAY_LIST, transformToJson(rayDAO.findAll())));
        }
        this.user = user;
        while (true) {
            Message message = receive();
            System.out.println(message.getMessageType());
            switch (message.getMessageType()) {
                case DATA: {
                    if (!message.getData().trim().equals(""))
                        sendBroadcastMessage(new Message(message.getMessageType(), "От " + user.getName() + ": " + message.getData().trim()));
                    break;
                }
                case BOOK_NUMBER_PLACE_TRY: {
                    bookNumberPlaceTry(message.getData());
                    break;
                }
                case BOOK_NUMBER_PLACE_CANCEL: {
                    bookNumberPlaceCancel(message.getData());
                    break;
                }
                case BOOK_PLACES_TRY: {
                    bookPlacesTry();
                    break;
                }
                case BOOK_PLACES_CANCEL: {
                    clearReadyTickets();
                    break;
                }
                case BUY_PLACES_TRY: {
                    buyPlacesTry(Double.parseDouble(message.getData()));
                    break;
                }
                case GET_TICKETS_LIST: {
                    sendUserTicketsList();
                    break;
                }
            }
        }
    }

    private void sendUserTicketsList() throws IOException {
        send(new Message(MessageType.MY_TICKETS_LIST,
                transformToJson(boughtOrBookTickets.stream().filter(ticket -> ticket.userName.equalsIgnoreCase(user.getName()))
                        .collect(Collectors.toCollection(LinkedList::new)))));
    }

    private void bookNumberPlaceTry(String json) throws IOException, GenericDAOException {
        Ticket ticket = null;
        try {
            ticket = transformFromJson(new TypeReference<Ticket>() {
            }, json);
        } catch (IOException ignore) {
        }
        if (ticket != null) {
            boolean ok = false;
            synchronized (lock_rays) {
                for (Ray ray : rayDAO.findAll()) {
                    if (ray.equals(ticket.ray)) {
                        Place place = ray.getPlaces().get(ticket.numberPlace);
                        if (place.getStatePlace() == StatePlace.FREE) {
                            place.setStatePlace(StatePlace.BOOK);
                            place.setName(user.getName());
                            rayDAO.updateById(ray.getId(), ray);
                            ticket.ray = ray;
                            readyBookTickets.add(ticket);
                            send(new Message(MessageType.BOOK_NUMBER_PLACE_OK, json));
                            ok = true;
                        }
                        break;
                    }
                }
                if (!ok) {
                    send(new Message(MessageType.BOOK_NUMBER_PLACE_ERROR, json));
                }
                sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rayDAO.findAll())));
            }
        }
    }

    private void bookNumberPlaceCancel(String json) throws IOException, GenericDAOException {
        Ticket ticket = null;
        try {
            ticket = transformFromJson(new TypeReference<Ticket>() {
            }, json);
        } catch (IOException ignore) {
        }
        if (ticket != null) {
            synchronized (lock_rays) {
                for (Ray ray : rayDAO.findAll()) {
                    if (ray.equals(ticket.ray)) {
                        Place place = ray.getPlaces().get(ticket.numberPlace);
                        if (place.getStatePlace() == StatePlace.BOOK && place.getName() != null && place.getName().equals(user.getName())) {
                            place.setStatePlace(StatePlace.FREE);
                            place.setName(null);
                            rayDAO.updateById(ray.getId(), ray);
                            if (readyBookTickets.contains(ticket))
                                readyBookTickets.remove(ticket);
                            else
                                boughtOrBookTickets.remove(ticket);
                            sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rayDAO.findAll())));
                        }
                        break;
                    }
                }
            }
        }
    }

    private void bookPlacesTry() throws IOException, GenericDAOException {
        if (readyBookTickets.size() > 0)
            synchronized (lock_rays) {
                for (Ray ray : rayDAO.findAll()) {
                    Iterator<Ticket> iterator = readyBookTickets.iterator();
                    boolean foundRay = false;
                    while (iterator.hasNext()) {
                        Ticket ticket = iterator.next();
                        if (ray.equals(ticket.ray)) {
                            Place place = ray.getPlaces().get(ticket.numberPlace);
                            if (place.getName() != null
                                    && place.getName().equalsIgnoreCase(user.getName())
                                    && place.getStatePlace() == StatePlace.BOOK) {
                                boughtOrBookTickets.add(ticket);
                            }
                            foundRay = true;
                        }
                    }
                    if (foundRay)
                        break;
                }
                readyBookTickets.clear();
                send(new Message(MessageType.BOOK_PLACES_OK));
                sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rayDAO.findAll())));
            }
    }

    private void buyPlacesTry(double idRay) throws IOException, GenericDAOException {
        boolean buy = false;
        synchronized (lock_rays) {
            for (Ray ray : rayDAO.findAll()) {
                if (ray.getId() == idRay) {
                    for (Place place : ray.places) {
                        if (place.getName() != null && place.getName().equalsIgnoreCase(user.getName()) && place.getStatePlace() == StatePlace.BOOK) {
                            buy = true;
                            place.setStatePlace(StatePlace.SAILED);
                            boughtOrBookTickets.add(new Ticket(ray, user.getName(), place.getNumber()));
                        }
                    }
                    rayDAO.updateById(ray.getId(), ray);
                    break;
                }
            }
            readyBookTickets.clear();
            if (buy)
                send(new Message(MessageType.BUY_PLACES_OK));
            sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rayDAO.findAll())));
        }
    }

    private void clearReadyTickets() throws IOException, GenericDAOException {
        if (readyBookTickets.size() > 0) {
            synchronized (lock_rays) {
                for (Ray ray : rayDAO.findAll()) {
                    Iterator<Ticket> iterator = readyBookTickets.iterator();
                    boolean foundRay = false;
                    while (iterator.hasNext()) {
                        Ticket ticket = iterator.next();
                        if (ray.equals(ticket.ray)) {
                            if (ray.getPlaces().get(ticket.numberPlace).getName() != null
                                    && ray.getPlaces().get(ticket.numberPlace).getName().equalsIgnoreCase(user.getName())
                                    && ray.getPlaces().get(ticket.numberPlace).getStatePlace() == StatePlace.BOOK) {
                                ray.getPlaces().get(ticket.numberPlace).setStatePlace(StatePlace.FREE);
                                ray.getPlaces().get(ticket.numberPlace).setName(null);
                                rayDAO.updateById(ray.getId(), ray);
                            }
                            foundRay = true;
                        } else break;
                    }
                    if (foundRay)
                        break;
                }
                readyBookTickets.clear();
                sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rayDAO.findAll())));
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            clearReadyTickets();
        } catch (GenericDAOException e) {
        }
        super.close();
    }
}
