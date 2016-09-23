package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public class Coordinates {
    public String country;
    public String city;

    public Coordinates(){}
    public Coordinates(String country, String city) {
        this.country = country;
        this.city = city;
    }

    @Override
    public String toString() {
        return country + ", " + city;
    }
}
