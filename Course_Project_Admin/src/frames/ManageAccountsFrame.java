package frames;

import client.ClientController;
import connection.MessageType;
import user.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Alexey on 08.10.2016.
 */
public class ManageAccountsFrame extends AbstractFrame{

    private DefaultListModel<User> listModelUsers;

    public ManageAccountsFrame(ClientController controller) {
        super(controller);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);


        listModelUsers = new DefaultListModel<>();

        JList<User> userJList = new JList<>(listModelUsers);
        userJList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                User user = (User) value;
                String innerString = "<html>\n" +
                        "<style>\n" +
                        "    div.wrapper {\n" +
                        "        border: 4px double;\n" +
                        "    }\n" +
                        "</style>\n" +
                        "<div class=\"wrapper\">\n" +
                        "    <div>\n" +
                        "        <span>" +
                        user.getName() +
                        "</span><br>\n" +
                        "        <span>Доступ: </span><span>" +
                        levelAccess[user.getAccess()] +
                        "</span>\n" +
                        "    </div>\n" +
                        "</div>\n" +
                        "</html>";
                label.setText(innerString);
                return label;
            }
        });

        add(userJList, BorderLayout.CENTER);
    }

    String[] levelAccess = {"Пользователь", "Модератор", "Пользователь(Запрос на получения прав модератора)"};

    @Override
    public void event(MessageType type, Object object) {
        switch (type){
            case LIST_USERS: {
                if(object != null && object instanceof ArrayList) {
                    listModelUsers.clear();
                    ((ArrayList<User>) object).stream().filter(user -> user.getAccess() >= 0).forEach(listModelUsers::addElement);
                    revalidate();
                }
                break;
            }
        }
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(500, 500);
    }
}
