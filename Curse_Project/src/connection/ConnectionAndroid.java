package connection;

import com.fasterxml.jackson.core.type.TypeReference;
import ray.Place;
import ray.Ray;
import ray.StatePlace;
import ticket.Ticket;
import user.User;

import static server.Server.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;


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

    @Override
    public void serverMainLoop(User user) throws IOException, ClassNotFoundException {
        synchronized (
                rays) {
            send(new Message(MessageType.RAY_LIST, transformToJson(rays)));
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

    private void bookNumberPlaceTry(String json) throws IOException {
        Ticket ticket = null;
        try {
            ticket = transformFromJson(new TypeReference<Ticket>() {
            }, json);
        } catch (IOException ignore) {
        }
        if (ticket != null) {
            boolean ok = false;
            synchronized (rays) {
                for (Ray ray : rays) {
                    if (ray.equals(ticket.ray)) {
                        if (ray.places[ticket.numberPlace].statePlace == StatePlace.FREE) {
                            ray.places[ticket.numberPlace].statePlace = StatePlace.BOOK;
                            ticket.ray = ray;
                            readyBookTickets.add(ticket);
                            ray.places[ticket.numberPlace].name = user.getName();
                            send(new Message(MessageType.BOOK_NUMBER_PLACE_OK, json));
                            ok = true;
                        }
                        break;
                    }
                }
                if (!ok) {
                    send(new Message(MessageType.BOOK_NUMBER_PLACE_ERROR, json));
                }
                sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rays)));
            }
        }
    }

    private void bookNumberPlaceCancel(String json) throws IOException {
        Ticket ticket = null;
        try {
            ticket = transformFromJson(new TypeReference<Ticket>() {
            }, json);
        } catch (IOException ignore) {
        }
        if (ticket != null) {
            synchronized (rays) {
                for (Ray ray : rays) {
                    if (ray.equals(ticket.ray)) {
                        if (ray.places[ticket.numberPlace].statePlace == StatePlace.BOOK) {
                            ray.places[ticket.numberPlace].statePlace = StatePlace.FREE;
                            ray.places[ticket.numberPlace].name = null;
                            if (readyBookTickets.contains(ticket))
                                readyBookTickets.remove(ticket);
                            else
                                boughtOrBookTickets.remove(ticket);
                            sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rays)));
                        }
                        break;
                    }
                }
            }
        }
    }

    private void bookPlacesTry() throws IOException {
        if (readyBookTickets.size() > 0)
            synchronized (rays) {
                for (Ray ray : rays) {
                    Iterator<Ticket> iterator = readyBookTickets.iterator();
                    boolean foundRay = false;
                    while (iterator.hasNext()) {
                        Ticket ticket = iterator.next();
                        if (ray.equals(ticket.ray)) {
                            if (ray.places[ticket.numberPlace].name != null
                                    && ray.places[ticket.numberPlace].name.equalsIgnoreCase(user.getName())
                                    && ray.places[ticket.numberPlace].statePlace == StatePlace.BOOK) {
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
                sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rays)));
            }
    }

    private void buyPlacesTry(double idRay) throws IOException {
        synchronized (rays) {
            boolean buy = false;
            for (Ray ray : rays) {
                if (ray.id == idRay) {
                    for (Place place : ray.places) {
                        if (place.name != null && place.name.equalsIgnoreCase(user.getName()) && place.statePlace == StatePlace.BOOK) {
                            buy = true;
                            place.statePlace = StatePlace.SAILED;
                            boughtOrBookTickets.add(new Ticket(ray, user.getName(), place.number));
                        }
                    }
                    break;
                }
            }
            readyBookTickets.clear();
            if (buy)
                send(new Message(MessageType.BUY_PLACES_OK));
            sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rays)));
        }
    }

    private void clearReadyTickets() throws IOException {
        if (readyBookTickets.size() > 0) {
            synchronized (rays) {
                for (Ray ray : rays) {
                    Iterator<Ticket> iterator = readyBookTickets.iterator();
                    boolean foundRay = false;
                    while (iterator.hasNext()) {
                        Ticket ticket = iterator.next();
                        if (ray.equals(ticket.ray)) {
                            if (ray.places[ticket.numberPlace].name != null
                                    && ray.places[ticket.numberPlace].name.equalsIgnoreCase(user.getName())
                                    && ray.places[ticket.numberPlace].statePlace == StatePlace.BOOK) {
                                ray.places[ticket.numberPlace].statePlace = StatePlace.FREE;
                                ray.places[ticket.numberPlace].name = null;
                            }
                            foundRay = true;
                        } else break;
                    }
                    if (foundRay)
                        break;
                }
                readyBookTickets.clear();
                sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rays)));
            }
        }
    }

    @Override
    public void close() throws IOException {
        clearReadyTickets();
        super.close();
    }
}
