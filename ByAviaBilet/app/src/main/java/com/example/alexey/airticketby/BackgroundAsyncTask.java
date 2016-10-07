package com.example.alexey.airticketby;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.alexey.airticketby.connection.Connection;
import com.example.alexey.airticketby.connection.Message;
import com.example.alexey.airticketby.ray.Place;
import com.example.alexey.airticketby.ray.Ray;
import com.example.alexey.airticketby.ticket.Ticket;
import com.example.alexey.airticketby.user.User;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BackgroundAsyncTask extends AsyncTask<Void, Message, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        try {
            while (!Thread.currentThread().isInterrupted() && MainActivity.connection != null) {
                Message message = MainActivity.connection.receive();
                if (message.getMessageType() != null) {
                    publishProgress(message);
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(MainActivity.context, "Слушатель бай-бай", Toast.LENGTH_SHORT).show();
        MainActivity.connectError();
    }

    @Override
    protected void onProgressUpdate(Message... values) {
        switch (MainActivity.nowConnectionState) {
            case AUTHORIZATION:
                communicationAuthorization(values[0]);
                break;
            case REGISTRATION:
                communicationRegistration(values[0]);
                break;
            case CONNECT:
                communicationConnect(values[0]);
                break;
        }
    }

    // AUTHORIZATION
    private void communicationAuthorization(Message message){
        switch (message.getMessageType()) {
            case USER_ACCEPTED: {
                try {
                    MainActivity.userName = Connection.transformFromJson(new TypeReference<User>() {}, message.getData()).name;
                } catch (IOException ignored) {}
                Toast.makeText(MainActivity.context, "Авторизация прошла успешно.", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.context, "Добро пожаловать " + MainActivity.userName, Toast.LENGTH_SHORT).show();
                MainActivity.connectSuccess();
                MainActivity.context.setEnterFragment();
                break;
            }
            case USER_NOT_FOUNDED: {
                Toast.makeText(MainActivity.context, "Авторизация не прошла.\n" +
                        "Пользователь с таким именем не найден", Toast.LENGTH_SHORT).show();
                break;
            }
            case USER_ALREADY_WORK: {
                Toast.makeText(MainActivity.context, "Авторизация не прошла.\n" +
                        "Этот аккаунт в данный момент уже используется", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        ConnectionFragment.editNameAuthorization.setEnabled(true);
        ConnectionFragment.editPasswordAuthorization.setEnabled(true);
        ConnectionFragment.buttonAuthorization.setEnabled(true);
    }


    // REGISTRATION
    private void communicationRegistration(Message message){
        switch (message.getMessageType()){
            case USER_REGISTERED:{
                try {
                    MainActivity.userName = Connection.transformFromJson(new TypeReference<User>() {}, message.getData()).name;
                } catch (IOException ignored) {}
                Toast.makeText(MainActivity.context, "Регистрация успешно прошла.\n" +
                        "Теперь вы зарегистрированы", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.context, "Добро пожаловать " + MainActivity.userName, Toast.LENGTH_SHORT).show();
                MainActivity.connectSuccess();
                MainActivity.context.setEnterFragment();
                break;
            }
            case USER_ALREADY_EXIST:{
                Toast.makeText(MainActivity.context, "Регистрация не прошла.\n" +
                        "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        RegistrationFragment.buttonReg.setEnabled(true);
    }


    // CONNECT
    private void communicationConnect(Message message){
        switch (message.getMessageType()) {
            case DATA: {
                MainWindowFragment.textReceive.setText(message.getData());
                break;
            }
            case RAY_LIST: {
                initListRays(message.getData());
                break;
            }
            case MY_TICKETS_LIST: {
                myTicketsList(message.getData());
                break;
            }
            case BOOK_NUMBER_PLACE_OK: {
                bookNumberPlaceOK(message.getData());
                break;
            }
            case BOOK_NUMBER_PLACE_ERROR: {
                bookNumberPlaceError(message.getData());
                break;
            }
            case BOOK_PLACES_OK: {
                Toast.makeText(MainActivity.context, "Выбранные билеты забронированы", Toast.LENGTH_SHORT).show();
                MainActivity.context.onBackPressed();
                break;
            }
            case BUY_PLACES_OK: {
                Toast.makeText(MainActivity.context, "Выбранные билеты куплены", Toast.LENGTH_SHORT).show();
                MainActivity.context.onBackPressed();
                break;
            }
        }
    }

    private void initListRays(String json) {
        try {
            MainActivity.rays = Connection.transformFromJson(new TypeReference<ArrayList<Ray>>() {
            }, json);

            if (MainActivity.fragment instanceof ItemListRaysFragment && MainWindowFragment.selectedRay != null) {
                boolean exist = false;
                for (Ray ray : MainActivity.rays) {
                    if (ray.equals(MainWindowFragment.selectedRay)) {
                        MainWindowFragment.selectedRay = ray;
                        ItemListRaysFragment.places.clear();
                        for (Place statePlace : ray.places) {
                            ItemListRaysFragment.places.add(new HashMap<String, Object>());
                        }
                        ItemListRaysFragment.numbersCheckedPlaces = new boolean[MainWindowFragment.selectedRay.places.length];
                        ItemListRaysFragment.simpleAdapterForPlaces.notifyDataSetChanged();
                        exist = true;
                        break;
                    }
                }
                if (!exist) { // if ray isn`t exist now
                    MainActivity.context.onBackPressed();
                }
            }

            MainWindowFragment.data.clear();
            for (Ray ray : MainActivity.rays) {
                MainWindowFragment.data.add(new HashMap<String, Object>());
            }
            MainWindowFragment.simpleAdapterForRays.notifyDataSetChanged();
        } catch (Exception ignored) {
        }
    }

    private void myTicketsList(String json) {
        try{
            MyTicketsFragment.tickets = Connection.transformFromJson(new TypeReference<ArrayList<Ticket>>() {}, json);

            MyTicketsFragment.myTicketsItems.clear();
            for (Ticket ticket : MyTicketsFragment.tickets){
                MyTicketsFragment.myTicketsItems.add(new HashMap<String, Object>());
            }
            MyTicketsFragment.simpleAdapterTickets.notifyDataSetChanged();
        } catch (Exception ignored) {
            Toast.makeText(MainActivity.context, "" + ignored, Toast.LENGTH_SHORT).show();
        }
    }

    private void bookNumberPlaceOK(String json) {
        try {
            int numberPlace = Connection.transformFromJson(new TypeReference<Ticket>() {
            }, json).numberPlace;
            Toast.makeText(MainActivity.context, "Место №"
                    + (numberPlace + 1)
                    + " забронировано", Toast.LENGTH_SHORT).show();
            try {
                ItemListRaysFragment.textCostPlace.setText(
                        String.valueOf(Double.parseDouble(ItemListRaysFragment.textCostPlace.getText().toString())
                        + MainWindowFragment.selectedRay.places[numberPlace].payment));
            } catch (Exception e){
                Toast.makeText(MainActivity.context, "" + e, Toast.LENGTH_LONG).show();
            }
        } catch (IOException ignored) {}
    }
    private void bookNumberPlaceError(String json) {
        try {
            Ticket ticket = Connection.transformFromJson(new TypeReference<Ticket>() {
            }, json);
            Toast.makeText(MainActivity.context, "Место №"
                    + (ticket.numberPlace + 1)
                    + " не удалось забронировать", Toast.LENGTH_SHORT).show();
        } catch (IOException ignored) {
        }
    }


}
