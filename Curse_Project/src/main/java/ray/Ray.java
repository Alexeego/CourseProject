package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dao.PlaceDAO;
import exceptions.GenericDAOException;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
@Entity
public class Ray {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;

    private StateRay stateRay = StateRay.NEW;
    private Date timeSending;
    private long timeInWay;
    private String numberRay;
    private int numberPlaces;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Place> places;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public StateRay getStateRay() {
        return stateRay;
    }

    public void setStateRay(StateRay stateRay) {
        this.stateRay = stateRay;
    }

    public Date getTimeSending() {
        return timeSending;
    }

    public void setTimeSending(Date timeSending) {
        this.timeSending = timeSending;
    }

    public long getTimeInWay() {
        return timeInWay;
    }

    public void setTimeInWay(long timeInWay) {
        this.timeInWay = timeInWay;
    }

    public String getNumberRay() {
        return numberRay;
    }

    public void setNumberRay(String numberRay) {
        this.numberRay = numberRay;
    }

    public int getNumberPlaces() {
        return numberPlaces;
    }

    public void setNumberPlaces(int numberPlaces) {
        this.numberPlaces = numberPlaces;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public Ray(){}
    public Ray(Coordinates coordinates, Date timeSending, long timeInWay, String numberRay, int numberPlaces) throws GenericDAOException {
        this.coordinates = coordinates;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = numberPlaces;
        initPlaces();
    }

    public Ray(Coordinates coordinates, StateRay stateRay, Date timeSending, long timeInWay, String numberRay, int numberPlaces) throws GenericDAOException {
        this.coordinates = coordinates;
        this.stateRay = stateRay;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = numberPlaces;
        initPlaces();
    }

    public Ray(Coordinates coordinates, StateRay stateRay, Date timeSending, long timeInWay, String numberRay, Place[] places) {
        this.coordinates = coordinates;
        this.stateRay = stateRay;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = places.length;
        this.places = Arrays.asList(places);
    }
    public Ray(Coordinates coordinates, Date timeSending, long timeInWay, String numberRay, Place[] places) {
        this.coordinates = coordinates;
        this.timeSending = timeSending;
        this.timeInWay = timeInWay * 60000;
        this.numberRay = numberRay;
        this.numberPlaces = places.length;
        this.places = Arrays.asList(places);
    }

    public Ray(Ray ray) {
        this.coordinates = ray.coordinates;
        this.stateRay = ray.stateRay;
        this.timeSending = ray.timeSending;
        this.timeInWay = ray.timeInWay;
        this.numberRay = ray.numberRay;
        this.numberPlaces = ray.numberPlaces;
        this.places = ray.places;
    }



    private void initPlaces() throws GenericDAOException {
        Place[] places = new Place[numberPlaces];
        double cost = (double) ((int) ((Math.random() * 101) * 100)) / 100d;
        for(int i = 0; i < numberPlaces;) {
            places[i] = new Place(cost, i++);
        }
        this.places = Arrays.asList(places);
    }

    @Override
    public String toString() {
        return "Ray{" +
                "id=" + id +
                ", coordinates=" + coordinates +
                ", stateRay=" + stateRay +
                ", timeSending=" + timeSending +
                ", timeInWay=" + timeInWay +
                ", numberRay='" + numberRay + '\'' +
                ", numberPlaces=" + numberPlaces +
                ", places=" + places +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ray ray = (Ray) o;
        return Long.compare(this.id, ray.id) == 0;
    }

    @Override
    public int hashCode() {
        int result = coordinates != null ? coordinates.hashCode() : 0;
        //result = 31 * result + (timeSending != null ? timeSending.hashCode() : 0);
        result = 31 * result + (int) (timeInWay ^ (timeInWay >>> 32));
        result = 31 * result + (numberRay != null ? numberRay.hashCode() : 0);
        result = 31 * result + numberPlaces;
        return result;
    }

    public void copy(Ray ray) throws GenericDAOException {
        PlaceDAO placeDAO = new PlaceDAO();
        this.coordinates = ray.coordinates;
        this.stateRay = ray.stateRay;
        this.timeSending.setTime(ray.timeSending.getTime());
        this.timeInWay = ray.timeInWay;
        this.numberRay = ray.numberRay;
        this.numberPlaces = ray.numberPlaces;
        for (int i = 0; i < places.size(); i++){
            Place place = this.places.get(i);
            place.setName(ray.places.get(i).getName());
            place.setStatePlace(ray.places.get(i).getStatePlace());
            placeDAO.updateById(place.getId(), place);
        }
    }
}
