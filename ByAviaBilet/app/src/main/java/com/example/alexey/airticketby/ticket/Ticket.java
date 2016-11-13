package com.example.alexey.airticketby.ticket;


import com.example.alexey.airticketby.ray.Ray;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public class Ticket {
    private Long id;
    public Ray ray;
    public String userName;
    public int numberPlace;

    public Ticket(){}
    public Ticket(Ray ray, String userName, int numberPlace) {
        this.ray = ray;
        this.userName = userName;
        this.numberPlace = numberPlace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ray getRay() {
        return ray;
    }

    public void setRay(Ray ray) {
        this.ray = ray;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNumberPlace() {
        return numberPlace;
    }

    public void setNumberPlace(int numberPlace) {
        this.numberPlace = numberPlace;
    }
}
