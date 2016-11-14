package connection;

import com.fasterxml.jackson.core.type.TypeReference;
import dao.CoordinatesDAO;
import dao.RayDAO;
import dao.UserDAO;
import exceptions.GenericDAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ray.Ray;
import user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

import static server.Server.*;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ConnectionAdmin extends Connection {

    protected ConnectionAdmin(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        super(socket, out, in);
    }

    public void send(Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message.getMessageType().toString());
            out.writeObject(message.getData());
            out.flush();
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (in) {
            MessageType type = MessageType.valueOf((String) in.readObject());
            String data = (String) in.readObject();
            return new Message(type, data);
        }
    }


    private static final Logger LOG = LoggerFactory.getLogger(ConnectionAdmin.class);

    private User user;
    private UserDAO userDAO = new UserDAO();
    private RayDAO rayDAO = new RayDAO();
    private CoordinatesDAO coordinatesDAO = new CoordinatesDAO();

    @Override
    public void serverMainLoop(User user) throws IOException, ClassNotFoundException, GenericDAOException {
        send(new Message(MessageType.RAY_LIST, transformToJson(rayDAO.findAll())));
        this.user = user;
        do {
            Message message = receive();
            switch (message.getMessageType()) {
                case DATA: {
                    if (!message.getData().trim().equals(""))
                        sendBroadcastMessage(new Message(message.getMessageType(), "Info от " + user.getName() + ": " + message.getData().trim()));
                    break;
                }
                case ADD_NEW_RAY: {
                    Ray ray = transformFromJson(new TypeReference<Ray>() {
                    }, message.getData());
                    synchronized (lock_rays) {
                        coordinatesDAO.insert(ray.getCoordinates());
                        rayDAO.insert(ray);
                        send(new Message(MessageType.NEW_RAY_ADDED));
                        lock_rays.notify();
                    }
                    break;
                }
                case EDIT_RAY: {
                    Ray ray = transformFromJson(new TypeReference<Ray>() {
                    }, message.getData());
                    synchronized (lock_rays) {
                        Optional<? extends Ray> result = rayDAO.findById(ray.getId());
                        if (result.isPresent()) {
                            result.get().setStateRay(ray.getStateRay());
                            result.get().setTimeSending(ray.getTimeSending());
                            rayDAO.updateById(result.get().getId(), result.get());
                        }
                        lock_rays.notify();
                    }
                    break;
                }
                case USER_SIGN_OUT: {
                    connectionMap.remove(user);
                    System.out.println("End run " + user.getName());
                    return;
                }
                case GET_LIST_USERS: {
                    send(new Message(MessageType.LIST_USERS, transformToJson(userDAO.findAll())));
                    break;
                }
                case DELETE_USER: {
                    User userEditable = transformFromJson(new TypeReference<User>() {
                    }, message.getData());
                    Optional<? extends User> result = userDAO.findByFields(userEditable.getName());
                    if (result.isPresent())
                        userDAO.deleteById(result.get().getId());
                    if (connectionMap.containsKey(userEditable)) {
                        connectionMap.get(userEditable).close();
                    }
                    send(new Message(MessageType.LIST_USERS, transformToJson(userDAO.findAll())));
                    break;
                }
                case EDIT_ACCESS_USER: {
                    User userEditable = transformFromJson(new TypeReference<User>() {
                    }, message.getData());
                    Optional<? extends User> result = userDAO.findByFields(userEditable.getName());

                    if (result.isPresent()) {
                        User userFromDB = result.get();
                        byte oldAccess = userFromDB.getAccess();
                        userFromDB.setAccess(userEditable.getAccess());
                        userDAO.updateById(userFromDB.getId(), userFromDB);
                        if (oldAccess == 1 && (userFromDB.getAccess() == 0 || userFromDB.getAccess() == 2)
                                && connectionMap.containsKey(userFromDB)
                                && connectionMap.get(userFromDB) instanceof ConnectionAdmin) {
                            connectionMap.get(userFromDB).close();
                        }
                    }
                    send(new Message(MessageType.LIST_USERS, transformToJson(userDAO.findAll())));
                    break;
                }
            }
        } while (true);
    }
}
