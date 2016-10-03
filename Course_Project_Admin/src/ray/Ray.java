package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public class Ray {
    @JsonIgnore
    static volatile AtomicLong id0 = new AtomicLong(0);

    public final double id = id0.incrementAndGet();
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
        initPlaces();
    }

    public Ray(Coordinates coordinates, StateRay stateRay, Date timeSending, long timeInWay, String numberRay, int numberPlaces) {
        this.coordinates = coordinates;
        this.stateRay = stateRay;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = numberPlaces;
        this.places = new Place[numberPlaces];
        initPlaces();
    }

    public Ray(Coordinates coordinates, StateRay stateRay, Date timeSending, long timeInWay, String numberRay, Place[] places) {
        this.coordinates = coordinates;
        this.stateRay = stateRay;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = places.length;
        this.places = places;
    }
    public Ray(Coordinates coordinates, Date timeSending, long timeInWay, String numberRay, Place[] places) {
        this.coordinates = coordinates;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = places.length;
        this.places = places;
    }


    private void initPlaces(){
        this.places = new Place[numberPlaces];
        double cost = Math.random() * 101;
        for(int i = 0; i < numberPlaces;) {
            this.places[i] = new Place(cost, i++);
        }
    }

    public static Place[] initPlaces(int count, double ePayment, int bSince, int bTo, double bPayment, int pSince, int pTo, double pPayment){
        Place[] places = new Place[count];
        for (int i = 0; i < count;){
            if(i + 1 >= bSince && i + 1 <= bTo){
                places[i] = new Place(TypeClass.BUSINESS, bPayment, i++);
            } else if(i + 1 >= pSince && i + 1 <= pTo){
                places[i] = new Place(TypeClass.PRIME, pPayment, i++);
            } else
                places[i] = new Place(TypeClass.ECONOMY, ePayment, i++);
        }
        return places;
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