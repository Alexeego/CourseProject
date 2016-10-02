package client;

import frames.AbstractFrame;
import ray.Ray;

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

    public void sendInfoMessage(String text) {
        model.sendInfoMessage(text);
    }

    public void openWindowForAddNewRay() {
        model.openWindowForAddNewRay();
    }

    public void toBackPressed(AbstractFrame abstractFrame) {
        model.toBackPressed(abstractFrame);
    }

    public void addNewInitRay(Ray ray) {
        model.addNewInitRay(ray);
    }

    public static void main(String[] args) {
        new ClientController();
    }

}
