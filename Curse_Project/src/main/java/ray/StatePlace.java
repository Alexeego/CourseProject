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

    StatePlace() {
    }

    StatePlace(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }

    public StatePlace valueOfByName(String s) {
        if (s != null)
            for (StatePlace statePlace : values()) {
                if (statePlace.s.equals(s))
                    return statePlace;
            }
        return null;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
