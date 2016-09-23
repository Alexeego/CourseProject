package com.example.alexey.airticketby.ticket;


import com.example.alexey.airticketby.ray.Ray;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public class Ticket {
    public Ray ray;
    public String userName;
    public int numberPlace;

    public Ticket(){}
    public Ticket(Ray ray, String userName, int numberPlace) {
        this.ray = ray;
        this.userName = userName;
        this.numberPlace = numberPlace;
    }
}
