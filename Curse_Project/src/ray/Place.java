package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Alexey on 10.09.2016.
 */
@JsonAutoDetect
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

    public StatePlace statePlace = StatePlace.FREE;
    public TypeClass typeClass = TypeClass.ECONOMY;
    public double payment;
    public int number;
    public String name = null;
}
