package server;

import com.fasterxml.jackson.core.type.TypeReference;
import connection.Connection;
import connection.ConnectionAdmin;
import connection.Message;
import connection.MessageType;
import ray.*;
import ticket.Ticket;
import ray.TypeClass;
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

    public static Map<String, User> allUsers = new ConcurrentHashMap<>();
    public static Map<User, Connection> connectionMap = new ConcurrentHashMap<>();
    public static final Set<Ray> rays = Collections.synchronizedSet(new LinkedHashSet<Ray>());
    public static final Set<Ticket> boughtOrBookTickets = Collections.synchronizedSet(new LinkedHashSet<Ticket>());

    static {
        rays.add(new Ray(new Coordinates("Italy", "Venetian"), new Date("12/15/2016"), 120, "F30I", 15));
        rays.add(new Ray(new Coordinates("Italy", "Rim"), StateRay.CANCEL, new Date("09/21/2016"), 140, "A32", 25));
        rays.add(new Ray(new Coordinates("Italy", "Vatican"), StateRay.SENDING, new Date("09/11/2016"), 140, "X12", 10));
        rays.add(new Ray(new Coordinates("USA", "New-York"), StateRay.COMPLETED, new Date("08/13/2016"), 300, "AD1", 30));
        rays.add(new Ray(new Coordinates("Russia", "Moscow"), StateRay.READY, new Date("09/22/2016"), 30, "0LH", 100));
        rays.add(new Ray(new Coordinates("Australia", "Sidney"), new Date("03/15/2017"), 250, "TY14", 50));
        Place[] places = new Place[5];
        places[0] = new Place(TypeClass.BUSINESS, 300, 0);
        places[1] = new Place(TypeClass.BUSINESS, 300, 1);
        places[2] = new Place(TypeClass.PRIME, 500, 2);
        places[3] = new Place(40, 3);
        places[4] = new Place(TypeClass.PRIME, 500, 4);
        rays.add(new Ray(new Coordinates("Japan", "Tokio"), new Date("01/15/2017"), 250, "JL13", places));

        allUsers.put("a", new User("a", "a", true));
        allUsers.put("ale", new User("ale", "a"));

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
                System.out.println("Connected");
                while (user == null) {
                    user = authorizationOrRegistration(connection);
                    if (user == null) continue;

                    System.out.println("New User = " + user.name);
                    connection.serverMainLoop(user);
                }
            } catch (Exception ignore) {
                System.out.println("Uoops! Run = " + ignore);
            }
            if (user != null) {
                connectionMap.remove(user);
                System.out.println("End run " + user.name);
            }
        }

        private User authorizationOrRegistration(Connection connection) throws IOException, ClassNotFoundException {
            do {
                Message answer = connection.receive();
                if (answer.getMessageType() == MessageType.USER_AUTHORIZATION) {
                    System.out.println("Authorization");
                    User user = Connection.transformFromJson(new TypeReference<User>() {}, answer.getData());
                    User userFromDB;
                    if (user != null && !user.Empty() && allUsers.containsKey(user.name) && user.equals((userFromDB = allUsers.get(user.name)))
                            && (!(connection instanceof ConnectionAdmin) || userFromDB.isAdmin())) {
                        connectionMap.put(user, connection);
                        connection.send(new Message(MessageType.USER_ACCEPTED, answer.getData()));
                        return user;
                    } else {
                        connection.send(new Message(MessageType.USER_NOT_FOUNDED));
                        return null;
                    }
                } else if(answer.getMessageType() == MessageType.USER_REGISTRATION){
                    System.out.println("Registration");
                    User user = Connection.transformFromJson(new TypeReference<User>() {}, answer.getData());
                    if (user != null && !user.Empty() && !allUsers.containsKey(user.name)) {
                        allUsers.put(user.name, user);
                        connectionMap.put(user, connection);
                        connection.send(new Message(MessageType.USER_REGISTERED, answer.getData()));
                        return user;
                    } else {
                        connection.send(new Message(MessageType.USER_ALREADY_EXIST));
                        return null;
                    }
                }
            } while (true);
        }
    }

    public static void sendBroadcastMessage(Message message, String... ignoreNames) {
        connectionMap.entrySet().stream()
                .filter(pair -> !(ignoreNames != null && ignoreNames.length > 0) || !pair.getKey().name.equals(ignoreNames[0]))
                .forEach(pair -> {
                    try {
                        pair.getValue().send(message);
                    } catch (IOException e) {
                        System.out.println("Ошибка. Сообщение не было доставлено.");
                    }
                });
    }

    public static void main(String... args) {
        int port = 1329;

//            String str = Connection.transformToJson(rays);
//            ArrayList<Ray> list = Connection.transformFromJson(new TypeReference<ArrayList<Ray>>(){}, str);
//            System.out.println(list.size());

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            do {
                Socket clientSocket = serverSocket.accept();
                System.out.println("new");
                new Handler(clientSocket).start();
            } while (true);
        } catch (IOException e) {
            System.out.println("Uoops! Main");
        }
    }
}
