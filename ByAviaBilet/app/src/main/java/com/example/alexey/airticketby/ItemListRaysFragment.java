package com.example.alexey.airticketby;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexey.airticketby.connection.Connection;
import com.example.alexey.airticketby.connection.Message;
import com.example.alexey.airticketby.connection.MessageType;
import com.example.alexey.airticketby.ray.Place;
import com.example.alexey.airticketby.ray.StatePlace;
import com.example.alexey.airticketby.ray.StateRay;
import com.example.alexey.airticketby.ticket.Ticket;


import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ItemListRaysFragment extends Fragment {


    public ItemListRaysFragment() {
        // Required empty public constructor
    }

    static Button buttonBookPlaces;
    static Button buttonBuyPlaces;
    ListView listView;
    TextView textCoordinatesRay;
    TextView textStateRay;
    static TextView textCostPlace;
    static boolean[] numbersCheckedPlaces = null;
    static List<Map<String, Object>> places = null;
    static SimpleAdapter simpleAdapterForPlaces = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list_rays, container, false);
        textCoordinatesRay = (TextView) view.findViewById(R.id.textCoordinatesRayFragment);
        textStateRay = (TextView) view.findViewById(R.id.textStateRayItemRays);
        textCostPlace = (TextView) view.findViewById(R.id.textCostPlace);
        textCostPlace.setText(String.valueOf(0d));

        buttonBookPlaces = (Button) view.findViewById(R.id.buttonBookPlaces);
        if (MainWindowFragment.selectedRay != null && MainWindowFragment.selectedRay.stateRay == StateRay.NEW)
            buttonBookPlaces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (numbersCheckedPlaces != null) {
                            for (boolean bool : numbersCheckedPlaces) {
                                if (bool) {
                                    MainActivity.connection.send(new Message(MessageType.BOOK_PLACES_TRY));
                                    break;
                                }
                            }
                        }
                    } catch (IOException e) {
                        MainActivity.connectError();
                    }
                }
            });
        else buttonBookPlaces.setVisibility(View.INVISIBLE);

        buttonBuyPlaces = (Button) view.findViewById(R.id.buttonBuyPlaces);

        if (MainWindowFragment.selectedRay != null
                && (MainWindowFragment.selectedRay.stateRay == StateRay.NEW
                || MainWindowFragment.selectedRay.stateRay == StateRay.READY))
            buttonBuyPlaces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (numbersCheckedPlaces != null) {
                        for (boolean bool : numbersCheckedPlaces) {
                            if (bool) {
                                try {

                                    textOverCost.setText(textCostPlace.getText().toString());
                                    dialog.show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.context, "2 = " + e, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                    }
                }
            });
        else buttonBuyPlaces.setVisibility(View.INVISIBLE);

        listView = (ListView) view.findViewById(R.id.listViewPlaces);
        places = new LinkedList<>();

        if (MainWindowFragment.selectedRay != null) {
            textCoordinatesRay.setText(MainWindowFragment.selectedRay.coordinates.toString());
            textStateRay.setText(MainWindowFragment.selectedRay.stateRay.toString());

            for (Place place : MainWindowFragment.selectedRay.places) {
                if (place.statePlace == StatePlace.BOOK && place.name.equalsIgnoreCase(MainActivity.userName)) {
                    try {
                        double payment = Double.parseDouble(textCostPlace.getText().toString()) + place.payment;
                        textCostPlace.setText(String.valueOf(payment));
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.context, "" + e.getClass(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            for (Place statePlace : MainWindowFragment.selectedRay.places) {
                places.add(new HashMap<String, Object>());
            }
            if (MainWindowFragment.selectedRay.places.length > 0) {
                numbersCheckedPlaces = new boolean[places.size()];
            }
        }

        createPayDialog();

        simpleAdapterForPlaces = new SimpleAdapter(getActivity(), places, R.layout.item_place_check, new String[]{}, new int[]{}) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View item = convertView;
                if (item == null) {
                    item = getActivity().getLayoutInflater().inflate(R.layout.item_place_check, parent, false);
                }
                ((TextView) item.findViewById(R.id.textTypeClassSpinnerItem))
                        .setText(MainWindowFragment.selectedRay.places[position].typeClass.toString());
                ((TextView) item.findViewById(R.id.textPaymentPlaceSpinnerItem))
                        .setText(String.format("%.2f", MainWindowFragment.selectedRay.places[position].payment));
                ((TextView) item.findViewById(R.id.textNumberPlaceSpinnerItem))
                        .setText(String.valueOf(MainWindowFragment.selectedRay.places[position].number + 1));
                ((TextView) item.findViewById(R.id.textStatePlaceOnRay))
                        .setText(MainWindowFragment.selectedRay.places[position].statePlace.toString());


                final CheckBox checkBox = (CheckBox) item.findViewById(R.id.checkBoxChoosePlace);
                if (MainWindowFragment.selectedRay.stateRay == StateRay.NEW || MainWindowFragment.selectedRay.stateRay == StateRay.READY) {

                    boolean bookMe = MainWindowFragment.selectedRay.places[position].statePlace == StatePlace.BOOK
                            && MainWindowFragment.selectedRay.places[position].name.equalsIgnoreCase(MainActivity.userName);

                    checkBox.setVisibility(View.VISIBLE);
                    if (MainWindowFragment.selectedRay.places[position].statePlace == StatePlace.FREE || bookMe) {
                        if (bookMe) {
                            numbersCheckedPlaces[position] = true;
                        }
                        checkBox.setEnabled(true);
                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean isChecked = !numbersCheckedPlaces[position];
                                try {
                                    if (isChecked) {
                                        checkBox.setEnabled(false);
                                        MainActivity.connection.send(new Message(MessageType.BOOK_NUMBER_PLACE_TRY,
                                                Connection.transformToJson(new Ticket(MainWindowFragment.selectedRay, MainActivity.userName, position))));
                                    } else {
                                        MainActivity.connection.send(new Message(MessageType.BOOK_NUMBER_PLACE_CANCEL,
                                                Connection.transformToJson(new Ticket(MainWindowFragment.selectedRay, MainActivity.userName, position))));
                                    }
                                } catch (IOException e) {
                                    MainActivity.connectError();
                                }
                            }
                        });
                    } else {
                        if (MainWindowFragment.selectedRay.places[position].statePlace == StatePlace.SAILED) {
                            checkBox.setVisibility(View.INVISIBLE);
                        }
                        checkBox.setEnabled(false);
                    }
                    checkBox.setChecked(numbersCheckedPlaces[position]);
                } else
                    checkBox.setVisibility(View.INVISIBLE);
                return item;
            }
        };
        listView.setAdapter(simpleAdapterForPlaces);
        return view;
    }

    AlertDialog dialog;
    TextView textOverCost;

    private void createPayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.context);
        builder.setTitle("Покупка выбранных билетов");
        View view = MainActivity.context.getLayoutInflater().inflate(R.layout.buying_dialog, null);
        textOverCost = (TextView) view.findViewById(R.id.textOverCost);
        view.findViewById(R.id.buttonPayForPlaces).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    MainActivity.connection.send(new Message(MessageType.BUY_PLACES_TRY, String.valueOf(MainWindowFragment.selectedRay.id)));
                } catch (IOException e) {
                    MainActivity.connectError();
                }

            }
        });
        view.findViewById(R.id.buttonDialogCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        numbersCheckedPlaces = null;
        simpleAdapterForPlaces = null;
        places = null;
        MainWindowFragment.selectedRay = null;
        try {
            MainActivity.connection.send(new Message(MessageType.BOOK_PLACES_CANCEL));
        } catch (Exception e) {
            MainActivity.connectError();
        }
    }
}
