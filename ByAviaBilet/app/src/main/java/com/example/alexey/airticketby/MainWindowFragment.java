package com.example.alexey.airticketby;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.alexey.airticketby.connection.Message;
import com.example.alexey.airticketby.connection.MessageType;
import com.example.alexey.airticketby.ray.Ray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainWindowFragment extends Fragment {


    public MainWindowFragment() {
        // Required empty public constructor
    }

    static TextView textReceive;
    EditText sendText;
    Button sendButton;
    ListView listRays;
    static LinkedList<Map<String, Object>> data = null;
    static SimpleAdapter simpleAdapterForRays = null;
    private static Integer positionRays = 0;

    static int[] colors = {R.color.colorNew, R.color.colorReady, R.color.colorSending, R.color.colorComplete, R.color.colorCancel};

    static Ray selectedRay = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_window, container, false);
        textReceive = (TextView) view.findViewById(R.id.textReceive);
        sendText = (EditText) view.findViewById(R.id.sendText);
        sendButton = (Button) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.connection != null) {
                    try {
                        textReceive.setText("send");
                        MainActivity.connection.send(new Message(MessageType.DATA, sendText.getText().toString()));
                    } catch (IOException ignore) {
                        MainActivity.connectError();
                    }
                }
            }
        });
        listRays = (ListView)view.findViewById(R.id.listView);
        data = new LinkedList<>();

        if(MainActivity.rays != null){
            for(Ray ray : MainActivity.rays){
                data.add(new HashMap<String, Object>());
            }
        }
        simpleAdapterForRays = new SimpleAdapter(MainActivity.context, data, R.layout.item_list_rays, new String[]{}, new int[]{}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View item = convertView;
                if(item == null){
                    item = getActivity().getLayoutInflater().inflate(R.layout.item_list_rays, parent, false);
                }
                Ray ray = MainActivity.rays.get(position);
                ((TextView) item.findViewById(R.id.itemId))
                        .setText(String.valueOf((int)ray.id));
                ((TextView) item.findViewById(R.id.itemCoordinates))
                        .setText(ray.coordinates.country + ", " + ray.coordinates.city);
                ((TextView) item.findViewById(R.id.itemDateSending))
                        .setText("Дата отправления:\n" + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(new Date(ray.timeSending.getTime() - 3600000)));
                ((TextView) item.findViewById(R.id.itemTimeInWay))
                        .setText("Время в пути:\n" + new SimpleDateFormat("HH:mm:ss").format(new Date(ray.timeInWay - 10800000)));
                TextView status = (TextView) item.findViewById(R.id.itemStatus);
                status.setText(ray.stateRay.toString());
                try{
                    status.setTextColor(getResources().getColor(colors[ray.stateRay.ordinal()]));
                } catch (Exception ignore){}
                return item;
            }
        };
        listRays.setAdapter(simpleAdapterForRays);
        listRays.setSelection(positionRays);
        listRays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MainActivity.rays != null) {
                    try {
                        selectedRay = null;
                        for (Ray ray : MainActivity.rays) {
                            if (ray.id == Integer.parseInt(((TextView) view.findViewById(R.id.itemId)).getText().toString())) {
                                selectedRay = ray;
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                MainActivity.stackFragments.push(MainActivity.fragment);
                                MainActivity.fragment = new ItemListRaysFragment();
                                fragmentManager.beginTransaction().
                                        replace(R.id.content_frame, MainActivity.fragment).commit();
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        positionRays = listRays.getFirstVisiblePosition();
    }
}
