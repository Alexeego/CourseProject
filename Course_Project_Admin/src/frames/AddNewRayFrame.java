package frames;

import client.ClientController;
import ray.Coordinates;
import ray.Ray;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Alexey on 01.10.2016.
 */
public class AddNewRayFrame extends AbstractFrame {

    private JTextField textFieldNewRayCoordinatesCountry;
    private JTextField textFieldNewRayCoordinatesCity;
    private JTextField textFieldNewRayTimeSending;
    private JTextField textFieldNewRayTimeInWay;
    private JTextField textFieldNewRayNumberRay;
    private JTextField textFieldNewRayCountPlaces;
    private JTextField textFieldNewRayEconomyPlacePayment;
    private JTextField textFieldNewRayBusinessPlacesSince;
    private JTextField textFieldNewRayBusinessPlacesTo;
    private JTextField textFieldNewRayBusinessPlacePayment;
    private JTextField textFieldNewRayPrimePlacesSince;
    private JTextField textFieldNewRayPrimePlacesTo;
    private JTextField textFieldNewRayPrimePlacePayment;

    private JButton buttonAddNewRay;
    private JButton buttonCancel;

    public AddNewRayFrame(ClientController controller) {
        super(controller);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new FlowLayout());
        setBackground(Color.DARK_GRAY);

        Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 13);


        JLabel labelAreaInfoNewRay = new JLabel("Добавление нового рейса");
        labelAreaInfoNewRay.setFont(new Font("Verdana", Font.ITALIC | Font.BOLD, 20));
        labelAreaInfoNewRay.setForeground(Color.WHITE);
        add(labelAreaInfoNewRay);

        JLabel labelAreaInfoNewRayCoordinates = new JLabel("<html><span style = 'color: red;'>* </span>Куда направляется рейс (Страна и Город)</html>");
        labelAreaInfoNewRayCoordinates.setFont(font);
        labelAreaInfoNewRayCoordinates.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayCoordinates);
        textFieldNewRayCoordinatesCountry = new JTextField(18);
        add(textFieldNewRayCoordinatesCountry);
        textFieldNewRayCoordinatesCity = new JTextField(18);
        add(textFieldNewRayCoordinatesCity);

        JLabel labelAreaInfoNewRayTimeSending = new JLabel("<html><span style = 'color: red;'>* </span>Дата отправления (укажите в формате 'дд.мм.гггг')</html>");
        labelAreaInfoNewRayTimeSending.setFont(font);
        labelAreaInfoNewRayTimeSending.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayTimeSending);
        textFieldNewRayTimeSending = new JTextField(10);
        add(textFieldNewRayTimeSending);

        JLabel labelHours = new JLabel("Часы:");
        labelHours.setFont(font);
        labelHours.setForeground(Color.WHITE);
        add(labelHours);
        JTextField textFieldNewRaySendingHours = new JTextField(2);
        add(textFieldNewRaySendingHours);
        JLabel labelMinutes = new JLabel("Минуты:");
        labelMinutes.setFont(font);
        labelMinutes.setForeground(Color.WHITE);
        add(labelMinutes);
        JTextField textFieldNewRaySendingMinutes = new JTextField(2);
        add(textFieldNewRaySendingMinutes);

        JLabel labelAreaInfoNewRayTimeInWay = new JLabel("<html><span style = 'color: red;'>* </span>Время в пути (укажите в минутах)</html>");
        labelAreaInfoNewRayTimeInWay.setFont(font);
        labelAreaInfoNewRayTimeInWay.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayTimeInWay);
        textFieldNewRayTimeInWay = new JTextField(30);
        add(textFieldNewRayTimeInWay);

        JLabel labelAreaInfoNewRayNumberRay = new JLabel("<html><span style = 'color: red;'>* </span>Номер рейса</html>");
        labelAreaInfoNewRayNumberRay.setFont(font);
        labelAreaInfoNewRayNumberRay.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayNumberRay);
        textFieldNewRayNumberRay = new JTextField(30);
        add(textFieldNewRayNumberRay);

        JLabel labelAreaInfoNewRayCountPlaces = new JLabel("<html><span style = 'color: red;'>* </span> Количество мест на борту самолёта.</html>");
        labelAreaInfoNewRayCountPlaces.setFont(font);
        labelAreaInfoNewRayCountPlaces.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayCountPlaces);
        textFieldNewRayCountPlaces = new JTextField(10);
        add(textFieldNewRayCountPlaces);

        JLabel labelAreaInfoNewRayAboutPlaces = new JLabel("<html>Все явно не выбранные места<br>" +
                "определяются <span style = 'color: yellow;'>Эконом типа</span></html>");
        labelAreaInfoNewRayAboutPlaces.setFont(font);
        labelAreaInfoNewRayAboutPlaces.setForeground(Color.WHITE);
        labelAreaInfoNewRayAboutPlaces.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        add(labelAreaInfoNewRayAboutPlaces);

        JLabel labelAreaInfoNewRayEconomyPlacePayment = new JLabel("<html><span style = 'color: red;'>* </span> Стоимость билетов эконом класса: </html>");
        labelAreaInfoNewRayEconomyPlacePayment.setFont(font);
        labelAreaInfoNewRayEconomyPlacePayment.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayEconomyPlacePayment);
        textFieldNewRayEconomyPlacePayment = new JTextField(10);
        add(textFieldNewRayEconomyPlacePayment);

        JLabel labelAreaInfoNewRayBusinessPlaces = new JLabel("Места бизнес класса (укажите промежуток От и По)");
        labelAreaInfoNewRayBusinessPlaces.setFont(font);
        labelAreaInfoNewRayBusinessPlaces.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayBusinessPlaces);
        textFieldNewRayBusinessPlacesSince = new JTextField(10);
        add(textFieldNewRayBusinessPlacesSince);
        textFieldNewRayBusinessPlacesTo = new JTextField(10);
        add(textFieldNewRayBusinessPlacesTo);
        JLabel labelAreaInfoNewRayBusinessPlacePayment = new JLabel("Стоимость билетов бизнес класса: ");
        labelAreaInfoNewRayBusinessPlacePayment.setFont(font);
        labelAreaInfoNewRayBusinessPlacePayment.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayBusinessPlacePayment);
        textFieldNewRayBusinessPlacePayment = new JTextField(10);
        add(textFieldNewRayBusinessPlacePayment);

        JLabel labelAreaInfoNewRayPrimePlaces = new JLabel("Места первого класса (укажите промежуток От и По)");
        labelAreaInfoNewRayPrimePlaces.setFont(font);
        labelAreaInfoNewRayPrimePlaces.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayPrimePlaces);
        textFieldNewRayPrimePlacesSince = new JTextField(10);
        add(textFieldNewRayPrimePlacesSince);
        textFieldNewRayPrimePlacesTo = new JTextField(10);
        add(textFieldNewRayPrimePlacesTo);
        JLabel labelAreaInfoNewRayPrimePlacePayment = new JLabel("Стоимость первого эконом класса: ");
        labelAreaInfoNewRayPrimePlacePayment.setFont(font);
        labelAreaInfoNewRayPrimePlacePayment.setForeground(Color.WHITE);
        add(labelAreaInfoNewRayPrimePlacePayment);
        textFieldNewRayPrimePlacePayment = new JTextField(10);
        add(textFieldNewRayPrimePlacePayment);

        buttonCancel = new JButton("Отмена");
        add(buttonCancel);
        buttonAddNewRay = new JButton("Добавить новый рейс");
        add(buttonAddNewRay);

        buttonAddNewRay.addActionListener(event -> {
            buttonAddNewRay.setEnabled(false);
            String number;
            try {
                boolean valid = true;

                // Coordinates
                String country = textFieldNewRayCoordinatesCountry.getText();
                String city = textFieldNewRayCoordinatesCity.getText();

                if (country.isEmpty() || city.isEmpty())
                    valid = false;

                // DateSending
                String hoursSending = textFieldNewRaySendingHours.getText().trim().equals("") ? "0" : textFieldNewRaySendingHours.getText();
                String minutesSending = textFieldNewRaySendingMinutes.getText().trim().equals("") ? "0" : textFieldNewRaySendingMinutes.getText();
                Date dateSending = null;
                if (valid) {
                    dateSending = validateDate(textFieldNewRayTimeSending.getText(), hoursSending, minutesSending);
                    valid = dateSending != null;
                }

                // TimeInWay
                String timeInWay = textFieldNewRayTimeInWay.getText();
                if (valid && !timeInWay.matches("^\\d+$"))
                    valid = false;

                // NumberRay
                String numberRay = textFieldNewRayNumberRay.getText();
                if (valid && numberRay.isEmpty())
                    valid = false;

                // Places
                int count = (number = textFieldNewRayCountPlaces.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                double ePayment = (number = textFieldNewRayEconomyPlacePayment.getText()).matches("^\\d+$") ? Double.parseDouble(number) : -1;

                int bSince = (number = textFieldNewRayBusinessPlacesSince.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                int bTo = (number = textFieldNewRayBusinessPlacesTo.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                double bPayment = (number = textFieldNewRayBusinessPlacePayment.getText()).matches("^\\d+$") ? Double.parseDouble(number) : -1;

                int pSince = (number = textFieldNewRayPrimePlacesSince.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                int pTo = (number = textFieldNewRayPrimePlacesTo.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                double pPayment = (number = textFieldNewRayPrimePlacePayment.getText()).matches("^\\d+$") ? Double.parseDouble(number) : -1;

                if (valid && count == -1 || ePayment < 1)
                    valid = false;
                if (valid && (bSince != -1 || bTo != -1) && !(bSince > 0 && bTo >= bSince && bTo <= count && (bTo < pSince || bSince > pTo) && bPayment > 0))
                    valid = false;
                if (valid && (pSince != -1 || pTo != -1) && !(pSince > 0 && pTo >= pSince && pTo <= count && (pTo < bSince || pSince > bTo) && pPayment > 0))
                    valid = false;

                if (valid) {
                    controller.addNewInitRay(new Ray(new Coordinates(country, city), dateSending, Long.parseLong(timeInWay), numberRay,
                            Ray.initPlaces(count, ePayment, bSince, bTo, bPayment, pSince, pTo, pPayment)));
                } else {
                    buttonAddNewRay.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "<html>Пожалуйста заполните все поля помеченные <span style='color: red;'>*</span>" +
                            ", а так же парные ячейки должны быть верно заполнены</html>", "Невнимательность - это плохо!", JOptionPane.INFORMATION_MESSAGE);
                }
                System.out.println("Валидность растановки мест: " + valid);
            } catch (Exception exception) {
                System.out.println(exception);
            }
        });

        buttonCancel.addActionListener(event -> {
            controller.toBackPressed(this);
        });
    }

    static Date validateDate(String dateString, String hoursSending, String minutesSending) {
        Date dateSending;
        if (!dateString.matches("^\\d{1,2}\\.\\d{1,2}\\.(\\d{2}|\\d{4})$") || !hoursSending.matches("^\\d{1,2}$") || !minutesSending.matches("^\\d{1,2}$")) {
            return null;
        } else {
            String[] dayAndMonthAndYear = dateString.split("\\.");
            dateSending = new Date(dayAndMonthAndYear[1] + "/" + dayAndMonthAndYear[0] + "/" + dayAndMonthAndYear[2]);
            if (!(Integer.parseInt(dayAndMonthAndYear[1]) == dateSending.getMonth() + 1
                    && Integer.parseInt(dayAndMonthAndYear[0]) == dateSending.getDate()
                    && Integer.parseInt(dayAndMonthAndYear[2]) == dateSending.getYear() + 1900
                    && Integer.parseInt(hoursSending) < 24 && Integer.parseInt(minutesSending) < 60)) {
                return null;
            } else {
                dateSending.setHours(Integer.parseInt(hoursSending));
                dateSending.setMinutes(Integer.parseInt(minutesSending));
                if (dateSending.getTime() <= new Date().getTime())
                    return null;
                return dateSending;
            }
        }

    }

    @Override
    public Dimension getDimension() {
        return new Dimension(450, 550);
    }

}
