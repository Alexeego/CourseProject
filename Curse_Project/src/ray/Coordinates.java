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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        return country != null ? country.equals(that.country) : that.country == null && (city != null ? city.equals(that.city) : that.city == null);

    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        return result;
    }
}
