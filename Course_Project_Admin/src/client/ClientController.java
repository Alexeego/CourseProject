package client;

import frames.AbstractFrame;
import ray.Ray;
import user.User;

import java.util.List;

/**
 * Created by Alexey on 09.07.2016.
 */
public class ClientController {

    private ClientController(){}

    private final ClientModel model = new ClientModel(new ClientView(this));

    public List<Ray> getListRays() {
        return model.getRays();
    }
    public boolean nowSysAdmin() {
        return model.nowSysAdmin();
    }

    public void tryConnection(String ip, int port){
        model.connectionToServer(ip, port);
    }

    public void authorization(String name, String password){
        model.authorization(name, password);
    }
    public void registration(String name, String password) {
        model.registration(name, password);
    }
    public void signOut() {
        model.signOut();
    }

    public void sendInfoMessage(String text) {
        model.sendInfoMessage(text);
    }

    public void addNewInitRay(Ray ray) {
        model.addNewInitRay(ray);
    }

    public void deleteUser(User user) {
        model.deleteUser(user);
    }
    public void editAccessUser(User user) {
        model.editAccessUser(user);
    }

    public void openWindowForAddNewRay() {
        model.openWindowForAddNewRay();
    }
    public void openWindowForRegistration() {
        model.openWindowForRegistration();
    }
    public void openWindowForManageAccounts() {
        model.openWindowForManageAccounts();
    }

    public void toBackPressed(AbstractFrame abstractFrame) {
        model.toBackPressed(abstractFrame);
    }

    // MAIN
    public static void main(String[] args) {
        new ClientController();
    }
}
