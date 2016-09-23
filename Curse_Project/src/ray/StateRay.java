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
}
