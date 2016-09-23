package client;

/**
 * Created by Alexey on 09.07.2016.
 */
public class ClientController {

    private final ClientModel model = new ClientModel(new ClientView(this));

    public void tryConnection(String ip, int port){
        model.connectionToServer(ip, port);
    }

    public void authorization(String name, String password){
        model.authorization(name, password);
    }

    public static void main(String[] args) {
        ClientController clientController = new ClientController();
    }
}
