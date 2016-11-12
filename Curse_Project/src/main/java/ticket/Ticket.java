package ticket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ray.Ray;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        return ray.equals(ticket.ray) && numberPlace == ticket.numberPlace && userName.equals(ticket.userName);

    }

    @Override
    public int hashCode() {
        int result = ray.hashCode();
        result = 31 * result + userName.hashCode();
        result = 31 * result + numberPlace;
        return result;
    }
}
