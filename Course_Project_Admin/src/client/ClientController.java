package client;

import frames.AbstractFrame;
import ray.Ray;
import user.User;

import java.util.List;

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

    public List<Ray> getListRays() {
        return model.getRays();
    }

    public static void main(String[] args) {
        new ClientController();
    }

    public void signOut() {
        model.signOut();
    }

    public void openWindowForRegistration() {
        model.openWindowForRegistration();
    }

    public boolean nowSysAdmin() {
        return model.nowSysAdmin();
    }

    public void openWindowForManageAccounts() {
        model.openWindowForManageAccounts();
    }


    public void registration(String name, String password) {
        model.registration(name, password);
    }

    public void deleteUser(User user) {
        model.deleteUser(user);
    }

    public void editAccessUser(User user) {
        model.editAccessUser(user);
    }
}
