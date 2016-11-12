package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by Alexey on 10.09.2016.
 */
@JsonAutoDetect
@Entity
public class Place {

    public Place(){}

    public Place(int number) {
        this.number = number;
    }

    public Place(double payment, int number) {
        this.payment = payment;
        this.number = number;
    }

    public Place(TypeClass typeClass, double payment, int number) {
        this.typeClass = typeClass;
        this.payment = payment;
        this.number = number;
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StatePlace statePlace = StatePlace.FREE;
    private TypeClass typeClass = TypeClass.ECONOMY;
    private double payment;
    private int number;
    private String name = null;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatePlace getStatePlace() {
        return statePlace;
    }

    public void setStatePlace(StatePlace statePlace) {
        this.statePlace = statePlace;
    }

    public TypeClass getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(TypeClass typeClass) {
        this.typeClass = typeClass;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", statePlace=" + statePlace +
                ", typeClass=" + typeClass +
                ", payment=" + payment +
                ", number=" + number +
                ", name='" + name + '\'' +
                '}';
    }

    public void copy(Place place) {
        this.name = place.name;
        this.statePlace = place.statePlace;
    }
}
