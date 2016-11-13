package server;

import com.fasterxml.jackson.core.type.TypeReference;
import connection.Connection;
import connection.ConnectionAdmin;
import connection.Message;
import connection.MessageType;
import dao.RayDAO;
import dao.TicketDAO;
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

    public static final Object lock_rays = new Object();



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
        }

        private RayDAO rayDAO = new RayDAO();
        private TicketDAO ticketDAO = new TicketDAO();

        @Override
        public void run() {
            try {
                synchronized (lock_rays) {
                    while (!isInterrupted()) {
                        Date nowDate = new Date();
                        rayDAO.findAll().forEach(ray -> {
                            try {
                                if (ray.getStateRay() != StateRay.CANCEL) {
                                    if (ray.getTimeSending().getTime() + ray.getTimeInWay() < nowDate.getTime())
                                        ray.setStateRay(StateRay.COMPLETED);
                                    else if (ray.getTimeSending().getTime() < nowDate.getTime())
                                        ray.setStateRay(StateRay.SENDING);
                                    else if (ray.getTimeSending().getTime() - 36000000 < nowDate.getTime()) {
                                        ray.setStateRay(StateRay.READY);
                                        List<Integer> placeNumbers = new LinkedList<>();
                                        ray.getPlaces().stream().filter(place -> place.getStatePlace() == StatePlace.BOOK)
                                                .forEach(place -> {
                                                    placeNumbers.add(place.getNumber());
                                                    place.setName(null);
                                                    place.setStatePlace(StatePlace.FREE);
                                                });
                                        ticketDAO.deleteAllBooksTicketsInRay(ray.getId(), placeNumbers);
                                    } else ray.setStateRay(StateRay.NEW);
                                    rayDAO.updateById(ray.getId(), ray);
                                }
                            } catch (GenericDAOException e) {
                                e.printStackTrace();
                            }
                        });
                        try {
                            sendBroadcastMessage(new Message(MessageType.RAY_LIST, Connection.transformToJson(rayDAO.findAll())));
                        } catch (IOException e) {
                        }
                        lock_rays.wait(60000);
                    }
                }
            } catch (InterruptedException | GenericDAOException ignored) {
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
            validateThread.start();
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
