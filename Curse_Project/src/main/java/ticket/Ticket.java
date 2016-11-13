package ticket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ray.Ray;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private int numberPlace;

    @ManyToOne
    @JoinColumn(name = "ray")
    private Ray ray;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;
        if(this.id != null && ticket.id != null) return Long.compare(this.id, ticket.id) == 0;
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
