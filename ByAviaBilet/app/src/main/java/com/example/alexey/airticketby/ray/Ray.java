package com.example.alexey.airticketby.ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Date;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public class Ray {
    public double id;
    public Coordinates coordinates;
    public StateRay stateRay = StateRay.NEW;
    public Date timeSending;
    public long timeInWay;
    public String numberRay;
    public int numberPlaces;
    public Place[] places;

    public Ray(){}
    public Ray(Coordinates coordinates, Date timeSending, long timeInWay, String numberRay, int numberPlaces) {
        this.coordinates = coordinates;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = numberPlaces;
        this.places = new Place[numberPlaces];
        double cost = Math.random() * 101;
        for(int i = 0; i < numberPlaces; i++){
            this.places[i] = new Place();
            this.places[i].payment = cost;
        }
    }

    public Ray(Coordinates coordinates, StateRay stateRay, Date timeSending, long timeInWay, String numberRay, int numberPlaces) {
        this.coordinates = coordinates;
        this.stateRay = stateRay;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = numberPlaces;
        this.places = new Place[numberPlaces];
        double cost = Math.random() * 101;
        for(int i = 0; i < numberPlaces; i++) {
            this.places[i] = new Place();
            this.places[i].payment = cost;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ray ray = (Ray) o;

        return Double.compare(ray.id, id) == 0;

    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(id);
        return (int) (temp ^ (temp >>> 32));
    }

}
