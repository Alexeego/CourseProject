package com.example.alexey.airticketby;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexey.airticketby.connection.Message;
import com.example.alexey.airticketby.connection.MessageType;
import com.example.alexey.airticketby.ray.Ray;
import com.example.alexey.airticketby.ray.StatePlace;
import com.example.alexey.airticketby.ticket.Ticket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyTicketsFragment extends Fragment {


    public MyTicketsFragment() {
        // Required empty public constructor
    }
    static ArrayList<Ticket> tickets;
    static List<Map<String, Object>> myTicketsItems = null;
    static SimpleAdapter simpleAdapterTickets = null;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_tickets, container, false);

        myTicketsItems = new ArrayList<>();

        simpleAdapterTickets = new SimpleAdapter(MainActivity.context, myTicketsItems, R.layout.item_list_tickets, new String[]{}, new int[]{}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View item = convertView;
                if(item == null){
                    item = getActivity().getLayoutInflater().inflate(R.layout.item_list_tickets, parent, false);
                }
                try {
                    Ticket ticket = tickets.get(position);
                    Ray ray = ticket.ray;
                    ((TextView)item.findViewById(R.id.textCoordinatesRayOnTicket)).setText(ray.coordinates.toString());
                    StatePlace state = ray.places[ticket.numberPlace].statePlace;
                    ((TextView)item.findViewById(R.id.textStatePlaceTicket)).setText("Место №" + (ticket.numberPlace + 1)  + " "
                            + (state == StatePlace.BOOK ? state.toString() : "Куплено"));
                    ((TextView)item.findViewById(R.id.textIdRayOnTicket)).setText(String.valueOf((int) ray.id));
                    ((TextView)item.findViewById(R.id.textStateRayInMyTicket)).setText(ticket.ray.stateRay.toString());

                } catch (Exception ignore){
                    Toast.makeText(MainActivity.context, "MY " + ignore.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return item;
            }
        };
        listView = (ListView)view.findViewById(R.id.listViewTickets);
        listView.setAdapter(simpleAdapterTickets);

        try {
            MainActivity.connection.send(new Message(MessageType.GET_TICKETS_LIST));
        } catch (IOException e) {
            MainActivity.connectError();
        }
        return view;
    }

}
