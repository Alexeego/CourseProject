package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public enum StateRay {
    NEW("Доступен для покупки билетов"),
    READY("Готовится к отправлению"),
    SENDING("Отправлен"),
    COMPLETED("Завершён"),
    CANCEL("Отменён");

    public String s;
    StateRay(){}
    StateRay(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }

    public StateRay valueOfByName(String s){
        if(s != null)
            for (StateRay stateRay: values()){
                if(stateRay.s.equals(s))
                    return stateRay;
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
