package frames;

import client.ClientController;
import ray.Ray;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by Alexey on 01.10.2016.
 */
public class AddNewRayFrame extends AbstractFrame{

    private JTextArea textAreaInfoNewRay;
    private JTextArea textAreaInfoNewRayCoordinates;
    private JTextArea textAreaInfoNewRayTimeSending;
    private JTextArea textAreaInfoNewRayTimeInWay;
    private JTextArea textAreaInfoNewRayNumberRay;
    private JTextArea textAreaInfoNewRayCountPlaces;
    private JTextArea textAreaInfoNewRayEconomyPlacePayment;
    private JTextArea textAreaInfoNewRayBusinessPlaces;
    private JTextArea textAreaInfoNewRayBusinessPlacePayment;
    private JTextArea textAreaInfoNewRayPrimePlaces;
    private JTextArea textAreaInfoNewRayPrimePlacePayment;

    private JTextField textFieldNewRayCoordinates;
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

        textAreaInfoNewRay = new JTextArea("Добавление нового рейса");
        textAreaInfoNewRay.setEditable(false);
        add(textAreaInfoNewRay);

        textAreaInfoNewRayCoordinates = new JTextArea("*Куда направляется рейс (укажите через запятую ',')");
        textAreaInfoNewRayCoordinates.setEditable(false);
        add(textAreaInfoNewRayCoordinates);
        textFieldNewRayCoordinates = new JTextField(30);
        add(textFieldNewRayCoordinates);

        textAreaInfoNewRayTimeSending = new JTextArea("*Дата отправления (укажите в формате 'ММ/дд/гггг')");
        textAreaInfoNewRayTimeSending.setEditable(false);
        add(textAreaInfoNewRayTimeSending);
        textFieldNewRayTimeSending = new JTextField(30);
        add(textFieldNewRayTimeSending);

        textAreaInfoNewRayTimeInWay = new JTextArea("*Время в пути (укажите в минутах)");
        textAreaInfoNewRayTimeInWay.setEditable(false);
        add(textAreaInfoNewRayTimeInWay);
        textFieldNewRayTimeInWay = new JTextField(30);
        add(textFieldNewRayTimeInWay);

        textAreaInfoNewRayNumberRay = new JTextArea("*Номер рейса");
        textAreaInfoNewRayNumberRay.setEditable(false);
        add(textAreaInfoNewRayNumberRay);
        textFieldNewRayNumberRay = new JTextField(30);
        add(textFieldNewRayNumberRay);

        textAreaInfoNewRayCountPlaces = new JTextArea("*Количество мест на борту самолёта.\n   ***Все явно не выбранные места\n   по стандарту определяются Эконом типа");
        textAreaInfoNewRayCountPlaces.setEditable(false);
        add(textAreaInfoNewRayCountPlaces);
        textFieldNewRayCountPlaces = new JTextField(10);
        add(textFieldNewRayCountPlaces);

        textAreaInfoNewRayEconomyPlacePayment = new JTextArea("*Стоимость билетов эконом класса");
        textAreaInfoNewRayEconomyPlacePayment.setEditable(false);
        add(textAreaInfoNewRayEconomyPlacePayment);
        textFieldNewRayEconomyPlacePayment = new JTextField(10);
        add(textFieldNewRayEconomyPlacePayment);

        textAreaInfoNewRayBusinessPlaces = new JTextArea("Места бизнес класса (укажите промежуток От и По)");
        textAreaInfoNewRayBusinessPlaces.setEditable(false);
        add(textAreaInfoNewRayBusinessPlaces);
        textFieldNewRayBusinessPlacesSince = new JTextField(10);
        add(textFieldNewRayBusinessPlacesSince);
        textFieldNewRayBusinessPlacesTo = new JTextField(10);
        add(textFieldNewRayBusinessPlacesTo);
        textAreaInfoNewRayBusinessPlacePayment = new JTextArea("Стоимость билетов бизнес класса");
        textAreaInfoNewRayBusinessPlacePayment.setEditable(false);
        add(textAreaInfoNewRayBusinessPlacePayment);
        textFieldNewRayBusinessPlacePayment = new JTextField(10);
        add(textFieldNewRayBusinessPlacePayment);

        textAreaInfoNewRayPrimePlaces = new JTextArea("Места первого класса (укажите промежуток От и По)");
        textAreaInfoNewRayPrimePlaces.setEditable(false);
        add(textAreaInfoNewRayPrimePlaces);
        textFieldNewRayPrimePlacesSince = new JTextField(10);
        add(textFieldNewRayPrimePlacesSince);
        textFieldNewRayPrimePlacesTo = new JTextField(10);
        add(textFieldNewRayPrimePlacesTo);
        textAreaInfoNewRayPrimePlacePayment = new JTextArea("Стоимость первого эконом класса");
        textAreaInfoNewRayPrimePlacePayment.setEditable(false);
        add(textAreaInfoNewRayPrimePlacePayment);
        textFieldNewRayPrimePlacePayment = new JTextField(10);
        add(textFieldNewRayPrimePlacePayment);

        buttonCancel = new JButton("Отмена");
        add(buttonCancel);
        buttonAddNewRay = new JButton("Добавить новый рейс");
        add(buttonAddNewRay);

        buttonAddNewRay.addActionListener(event -> {
            String number;
            try {
                int count = (number = textFieldNewRayCountPlaces.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                double ePayment = (number = textFieldNewRayEconomyPlacePayment.getText()).matches("^\\d+$") ? Double.parseDouble(number) : -1;

                int bSince = (number = textFieldNewRayBusinessPlacesSince.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                int bTo = (number = textFieldNewRayBusinessPlacesTo.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                double bPayment = (number = textFieldNewRayBusinessPlacePayment.getText()).matches("^\\d+$") ? Double.parseDouble(number) : -1;

                int pSince = (number = textFieldNewRayPrimePlacesSince.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                int pTo = (number = textFieldNewRayPrimePlacesTo.getText()).matches("^\\d+$") ? Integer.parseInt(number) : -1;
                double pPayment = (number = textFieldNewRayPrimePlacePayment.getText()).matches("^\\d+$") ? Double.parseDouble(number) : -1;

                boolean valid = true;
                if(count == -1 || ePayment < 1)
                    valid = false;
                if(valid && (bSince != -1 || bTo != -1) && !(bSince > 0 && bTo >= bSince && bTo <= count && (bTo < pSince || bSince > pTo) && bPayment > 0))
                    valid = false;
                if(valid && (pSince != -1 || pTo != -1) && !(pSince > 0 && pTo >= pSince && pTo <= count && (pTo < bSince || pSince > bTo) && pPayment > 0))
                    valid = false;

                if(valid){
                    System.out.println(Arrays.toString(Ray.initPlaces(count, ePayment, bSince, bTo, bPayment, pSince, pTo, pPayment)));
                }
                System.out.println("Валидность растановки мест: " + valid);
            } catch (Exception exception){
                System.out.println(exception);
            }
        });

        buttonCancel.addActionListener(event -> {
            controller.toBackPressed(this);
        });
    }

}
