package server;

import com.fasterxml.jackson.core.type.TypeReference;
import connection.Connection;
import connection.ConnectionAdmin;
import connection.Message;
import connection.MessageType;
import dao.UserDAO;
import db.AwareExecutor;
import exceptions.GenericDAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ray.*;
import ticket.Ticket;
import user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alexey on 07.09.2016.
 */
public class Server {


    private static final Logger LOG = LoggerFactory.getLogger(Server.class);


    public static Map<User, Connection> connectionMap = new ConcurrentHashMap<>();

    public static final Set<Ray> rays = Collections.synchronizedSet(new TreeSet<>((o1, o2) -> o1.equals(o2) ? 0 : Double.compare(o2.hashCode(), o1.hashCode())));
    public static final Set<Ticket> boughtOrBookTickets = Collections.synchronizedSet(new TreeSet<Ticket>((o1, o2) -> {
        int result = o1.userName.compareTo(o2.userName);
        if (result != 0) return result;
        result = o1.ray.coordinates.toString().compareTo(o2.ray.coordinates.toString());
        if (result != 0) return result;
        return Integer.compare(o1.numberPlace, o2.numberPlace);
    }));

    static {

        rays.add(new Ray(new Coordinates("Italy", "Venetian"), new Date("12/15/2016"), 120, "F30I", 15));
        rays.add(new Ray(new Coordinates("Italy", "Rim"), StateRay.NEW, new Date("09/21/2016"), 140, "A32", 25));
        rays.add(new Ray(new Coordinates("Italy", "Rim"), StateRay.CANCEL, new Date("09/20/2016"), 140, "A32", 25));
        rays.add(new Ray(new Coordinates("Italy", "Vatican"), StateRay.SENDING, new Date("09/11/2016"), 140, "X12", 10));
        rays.add(new Ray(new Coordinates("USA", "New-York"), StateRay.COMPLETED, new Date("08/13/2016"), 300, "AD1", 30));
        rays.add(new Ray(new Coordinates("Russia", "Moscow"), StateRay.NEW, new Date("10/10/2016"), 30, "0LH", 100));
        rays.add(new Ray(new Coordinates("Australia", "Sidney"), new Date("03/15/2017"), 250, "TY14", 50));
        Place[] places = new Place[5];
        places[0] = new Place(TypeClass.BUSINESS, 300, 0);
        places[1] = new Place(TypeClass.BUSINESS, 300, 1);
        places[2] = new Place(TypeClass.PRIME, 500, 2);
        places[3] = new Place(40, 3);
        places[4] = new Place(TypeClass.PRIME, 500, 4);
        Ray ray = new Ray(new Coordinates("Japan", "Tokio"), new Date("10/10/2016"), 250, "JL13", places);
        ray.timeSending.setHours(7);
        ray.timeSending.setMinutes(8);
        rays.add(ray);


    }

    private static class Handler extends Thread {
        private Socket clientSocket;

        Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            User user = null;
            try (Connection connection = Connection.build(clientSocket)) {
                LOG.info("Connected");
                while (true) {
                    user = null;
                    user = authorizationOrRegistration(connection);
                    if (user == null) continue;

                    LOG.info("New User = " + user.getName());
                    connection.serverMainLoop(user);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
                System.out.println("Uoops! Run = " + e);
            }
            if (user != null) {
                connectionMap.remove(user);
                LOG.info("End run " + user.getName());
            }
        }

        private User authorizationOrRegistration(Connection connection) throws IOException, ClassNotFoundException, GenericDAOException {
            do {
                Message answer = connection.receive();
                if (answer.getMessageType() == MessageType.USER_AUTHORIZATION) {
                    return authorization(connection, answer);
                } else if (answer.getMessageType() == MessageType.USER_REGISTRATION) {
                    return registration(connection, answer);
                }
            } while (true);
        }

        private User authorization(Connection connection, Message answer) throws IOException, GenericDAOException {
            User user = Connection.transformFromJson(new TypeReference<User>() {
            }, answer.getData());
            UserDAO userDAO = new UserDAO();
            User userFromDB;
            if (user != null && !user.Empty()
                    && userDAO.findByField(user.getName()).isPresent() && user.equals(userFromDB = userDAO.findByField(user.getName()).get())
                    && (!(connection instanceof ConnectionAdmin) || (userFromDB.getAccess() != 0 && userFromDB.getAccess() != 2))) {
                if (!connectionMap.containsKey(userFromDB)) {
                    connectionMap.put(userFromDB, connection);
                    if (connection instanceof ConnectionAdmin)
                        connection.send(new Message(MessageType.USER_ACCEPTED, Connection.transformToJson(userFromDB)));
                    else
                        connection.send(new Message(MessageType.USER_ACCEPTED, answer.getData()));
                    return userFromDB;
                } else {
                    connection.send(new Message(MessageType.USER_ALREADY_WORK));
                }
            } else {
                connection.send(new Message(MessageType.USER_NOT_FOUNDED));
            }
            return null;
        }

        private User registration(Connection connection, Message answer) throws IOException, GenericDAOException {
            User user = Connection.transformFromJson(new TypeReference<User>() {
            }, answer.getData());
            UserDAO userDAO = new UserDAO();
            if (user != null && !user.Empty() && !userDAO.findByField(user.getName()).isPresent()) {
                if (connection instanceof ConnectionAdmin) {
                    // TODO inspection first admin or already exists in DB
                    if (userDAO.findAll().stream().filter(userFromDB -> !userFromDB.getName().equalsIgnoreCase("alexey") && (userFromDB.getAccess() == -1))
                            .findFirst().orElse(null) == null)

                        user = new User(user.getName(), user.getPassword(), true);
                    else user = new User(user.getName(), user.getPassword(), false);
                } else user = new User(user.getName(), user.getPassword());

                userDAO.insert(user);

                if (connection instanceof ConnectionAdmin) {
                    connection.send(new Message(MessageType.USER_REGISTERED, Connection.transformToJson(user)));
                    return null;
                } else {
                    connectionMap.put(user, connection);
                    connection.send(new Message(MessageType.USER_REGISTERED, answer.getData()));
                    return user;
                }
            } else {
                connection.send(new Message(MessageType.USER_ALREADY_EXIST));
            }
            return null;
        }
    }


    public static Thread validateThread = new Thread() {
        {
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            try {
                synchronized (rays) {
                    while (!isInterrupted()) {
                        Date nowDate = new Date();
                        rays.stream().forEach(ray -> {
                            if (ray.stateRay != StateRay.CANCEL) {
                                if (ray.timeSending.getTime() + ray.timeInWay < nowDate.getTime())
                                    ray.stateRay = StateRay.COMPLETED;
                                else if (ray.timeSending.getTime() < nowDate.getTime())
                                    ray.stateRay = StateRay.SENDING;
                                else if (ray.timeSending.getTime() - 36000000 < nowDate.getTime()) {
                                    ray.stateRay = StateRay.READY;
                                    Set<Ticket> tickets = new LinkedHashSet<Ticket>();
                                    Arrays.stream(ray.places).filter(place -> place.statePlace == StatePlace.BOOK)
                                            .forEach(place -> {
                                                tickets.add(new Ticket(ray, place.name, place.number));
                                                place.name = null;
                                                place.statePlace = StatePlace.FREE;
                                            });
                                    boughtOrBookTickets.removeAll(tickets);
                                } else ray.stateRay = StateRay.NEW;
                            }
                        });
                        try {
                            sendBroadcastMessage(new Message(MessageType.RAY_LIST, Connection.transformToJson(rays)));
                        } catch (IOException e) {
                        }
                        rays.wait(60000);
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    };

    public static void sendBroadcastMessage(Message message, String... ignoreNames) {
        connectionMap.entrySet().stream()
                .filter(pair -> ignoreNames.length < 1 || !pair.getKey().getName().equals(ignoreNames[0]))
                .forEach(pair -> {
                    try {
                        pair.getValue().send(message);
                    } catch (IOException ignored) {
                        LOG.warn("Ошибка. Сообщение не было доставлено.");
                    }
                });
    }

    public static void main(String... args) throws IOException {
//            String str = Connection.transformToJson(rays);
//            ArrayList<Ray> list = Connection.transformFromJson(new TypeReference<ArrayList<Ray>>(){}, str);
//            System.out.println(list.size());

//        try {
//            AwareExecutor.initializationDataBase();
//            UserDAO userDAO = new UserDAO();
//
//            System.out.println("\n\nFrom DB");
//
//            List<User> usersFromDB = userDAO.findAll();
//            usersFromDB.forEach(System.out::println);
//
//        } catch (GenericDAOException e) {
//            e.printStackTrace();
//        }

        int port = Integer.parseInt(AwareExecutor.getEnvironmentProperties().getProperty("server_port"));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            AwareExecutor.initializationDataBase();
            do {
                Socket clientSocket = serverSocket.accept();
                LOG.info("new connect");
                new Handler(clientSocket).start();
            } while (true);
        } catch (Exception e) {
            LOG.error("Uoops! Main\n" + e.getMessage());
        }
    }
}
