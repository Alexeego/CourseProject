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
    private JList<User> userJList;
    private JPanel editPanel;
    private JLabel nameSelectedUserLabel;
    private JComboBox<String> comboBox;

    public ManageAccountsFrame(ClientController controller) {
        super(controller);
    }

    @Override
    protected void initializationWindow() {
        setLayout(null);
        setBackground(Color.DARK_GRAY);


        String[] colorsText = {"black", "white", "blue"};
        String[] colorsDiv = {"white", "black", "yellow"};
        String[] levelAccess = {"Пользователь", "Модератор", "Пользователь(Запрос на получения прав модератора)"};

        JPanel editPanel = new JPanel();
        editPanel.setVisible(false);
        editPanel.setBounds(242, 0, 259, 260);
        add(editPanel);
        editPanel.setLayout(null);

        nameSelectedUserLabel = new JLabel("");
        nameSelectedUserLabel.setBounds(30, 5, 200, 40);
        editPanel.add(nameSelectedUserLabel);

        comboBox = new JComboBox<>(levelAccess);
        comboBox.setBounds(5, 50, 240, 30);
        editPanel.add(comboBox);

        JButton buttonEdit = new JButton("Внести изменения");
        buttonEdit.setBounds(30, 90, 200, 30);
        editPanel.add(buttonEdit);
        String[] optionsEdit = {"Да, изменить", "Нет, не изменять"};
        buttonEdit.addActionListener(event -> {
            if(!userJList.isSelectionEmpty() && userJList.getSelectedValue().getAccess() != comboBox.getSelectedIndex()){
                int i = JOptionPane.showOptionDialog(this, "Изменить права доступа пользователя?" + userJList.getSelectedValue().getName() + "?",
                        "Удаление", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsEdit, optionsEdit[1]);
                if(i == 0){
                    editPanel.setVisible(false);
                    User userFromList = userJList.getSelectedValue();
                    userFromList.setAccess((byte)comboBox.getSelectedIndex());
                    controller.editAccessUser(userJList.getSelectedValue());
                }
            }
        });

        JButton buttonDelete = new JButton("Удалить пользователя");
        buttonDelete.setBounds(30, 130, 200, 30);
        editPanel.add(buttonDelete);
        String[] optionsDelete = {"Да, удалить", "Нет, не удалять"};
        buttonDelete.addActionListener(event -> {
            if(!userJList.isSelectionEmpty()){
                int i = JOptionPane.showOptionDialog(this, "Удалить пользователя?" + userJList.getSelectedValue().getName() + "?",
                        "Удаление", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsDelete, optionsDelete[1]);
                if(i == 0){
                    editPanel.setVisible(false);
                    controller.deleteUser(userJList.getSelectedValue());
                }
            }
        });


        listModelUsers = new DefaultListModel<>();

        userJList = new JList<>(listModelUsers);
        userJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userJList.addListSelectionListener(event -> {
            if(!userJList.isSelectionEmpty()){
                nameSelectedUserLabel.setText("<html><h2 style = 'color: red;'>" + userJList.getSelectedValue().getName() + "</h2></html>");
                comboBox.setSelectedIndex(userJList.getSelectedValue().getAccess());
                editPanel.setVisible(true);
            }
        });
        userJList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                User user = (User) value;
                String innerString = "<html>\n" +
                        "<style>\n" +
                        "    div.wrapper {\n" +
                        "        border: 4px double;\n" +
                        "        width: 168px;\n" +
                        "        background: " + colorsDiv[user.getAccess()] + ";" +
                        "    }\n" +
                        "</style>\n" +
                        "<div class=\"wrapper\">\n" +
                        "    <div>\n" +
                        "        <span style = 'font-size: 20px; color: " + colorsText[user.getAccess()] + ";'> " +
                        user.getName() +
                        "</span><br>\n" +
                        "        <span style = 'color: " + colorsText[user.getAccess()] + ";'> Доступ: </span>\n" +
                        "<span style = 'color: " + colorsText[user.getAccess()] + ";'>" +
                        levelAccess[user.getAccess()] +
                        "</span>\n" +
                        "    </div>\n" +
                        "</div>\n" +
                        "</html>";
                label.setText(innerString);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(userJList);
        scrollPane.setBounds(0, 0, 238, 260);
        add(scrollPane);

        JButton buttonCancel = new JButton("Назад");
        buttonCancel.setBounds(150, 265, 200, 30);
        add(buttonCancel);
        buttonCancel.addActionListener(event -> controller.toBackPressed(this));

    }


    @Override
    public void event(MessageType type, Object object) {
        switch (type){
            case LIST_USERS: {
                if(object != null && object instanceof ArrayList) {
                    listModelUsers.clear();
                    ((ArrayList<User>) object).stream().filter(user -> user.getAccess() >= 0).forEach(listModelUsers::addElement);
                    SwingUtilities.invokeLater(this::revalidate);
                }
                break;
            }
        }
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(500, 325);
    }
}
