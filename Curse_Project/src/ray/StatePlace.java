package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public enum StatePlace {
    FREE("Свободно"),
    BOOK("Забронировано"),
    SAILED("Продано");

    public String s;
    StatePlace(){}
    StatePlace(String s){
        this.s = s;
    }
    @Override
    public String toString() {
        return s;
    }
}
