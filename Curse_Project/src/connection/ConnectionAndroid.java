package connection;
import com.fasterxml.jackson.core.type.TypeReference;
import ray.Place;
import ray.Ray;
import ray.StatePlace;
import server.Server;
import ticket.Ticket;
import user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
        synchronized (out){
            out.writeObject(message.getMessageType().toString());
            out.writeObject(message.getData());
            out.flush();
        }
    }

    @Override
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (in){
            MessageType type = MessageType.valueOf((String)in.readObject());
            String data = (String) in.readObject();
            return new Message(type, data);
        }
    }


    private List<Ticket> readyBookTickets = new LinkedList<>();
    private User user;

    @Override
    public void serverMainLoop(User user) throws IOException, ClassNotFoundException {
        synchronized (Server.rays) {
            send(new Message(MessageType.RAY_LIST, Connection.transformToJson(Server.rays)));
        }
        this.user = user;
        while (true) {
            Message message = receive();
            System.out.println(message.getMessageType());
            switch (message.getMessageType()) {
                case DATA: {
                    System.out.println(message.getData());
                    Server.sendBroadcastMessage(message);
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
                transformToJson(Server.boughtOrBookTickets.stream().filter(ticket -> ticket.userName.equals(user.name))
                .collect(Collectors.toCollection(LinkedList::new)))));
    }

    private void bookNumberPlaceTry(String json) throws IOException {
        Ticket ticket = null;
        try {
            ticket = transformFromJson(new TypeReference<Ticket>() {
            }, json);
        } catch (IOException ignore) {}
        if(ticket != null) {
            boolean ok = false;
            synchronized (Server.rays) {
                for (Ray ray : Server.rays) {
                    if (ray.equals(ticket.ray)) {
                        if (ray.places[ticket.numberPlace].statePlace == StatePlace.FREE) {
                            ray.places[ticket.numberPlace].statePlace = StatePlace.BOOK;
                            readyBookTickets.add(ticket);
                            ray.places[ticket.numberPlace].name = user.name;
                            send(new Message(MessageType.BOOK_NUMBER_PLACE_OK, json));
                            ok = true;
                        }
                        break;
                    }
                }
                if (!ok) {
                    send(new Message(MessageType.BOOK_NUMBER_PLACE_ERROR, json));
                }
                Server.sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(Server.rays)));
            }
        }
    }
    private void bookNumberPlaceCancel(String json) throws IOException {
        Ticket ticket = null;
        try {
            ticket = transformFromJson(new TypeReference<Ticket>() {
            }, json);
        } catch (IOException ignore) {}
        if(ticket != null) {
            synchronized (Server.rays) {
                for (Ray ray : Server.rays) {
                    if (ray.equals(ticket.ray)) {
                        if (ray.places[ticket.numberPlace].statePlace == StatePlace.BOOK) {
                            ray.places[ticket.numberPlace].statePlace = StatePlace.FREE;
                            ray.places[ticket.numberPlace].name = null;
                            if (readyBookTickets.contains(ticket))
                                readyBookTickets.remove(ticket);
                            else Server.boughtOrBookTickets.remove(ticket);
                            Server.sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(Server.rays)));
                        }
                        break;
                    }
                }
            }
        }
    }

    private void bookPlacesTry() throws IOException {
        Server.boughtOrBookTickets.addAll(readyBookTickets);
        readyBookTickets.clear();
        send(new Message(MessageType.BOOK_PLACES_OK));
        synchronized (Server.rays) {
            Server.sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(Server.rays)));
        }
    }
    private void buyPlacesTry(double idRay) throws IOException {
        synchronized (Server.rays) {
            boolean buy = false;
            for (Ray ray : Server.rays) {
                if (ray.id == idRay) {
                    for (Place place : ray.places) {
                        if (place.name != null && place.name.equals(user.name)) {
                            buy = true;
                            place.statePlace = StatePlace.SAILED;
                        }
                    }
                    Server.boughtOrBookTickets.addAll(readyBookTickets);
                    readyBookTickets.clear();
                    break;
                }
            }
            if (buy)
                send(new Message(MessageType.BUY_PLACES_OK));
            Server.sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(Server.rays)));
        }
    }

    private void clearReadyTickets() throws IOException {
        if (readyBookTickets != null && readyBookTickets.size() > 0) {
            synchronized (Server.rays) {
                for (Ray ray : Server.rays) {
                    Iterator<Ticket> iterator = readyBookTickets.iterator();
                    while (iterator.hasNext()) {
                        Ticket ticket = iterator.next();
                        if (ray.equals(ticket.ray)) {
                            ray.places[ticket.numberPlace].statePlace = StatePlace.FREE;
                            ray.places[ticket.numberPlace].name = null;
                            iterator.remove();
                        }
                    }
                }
                Server.sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(Server.rays)));
            }
        }
    }

    @Override
    public void close() throws IOException {
        clearReadyTickets();
        super.close();
    }
}
