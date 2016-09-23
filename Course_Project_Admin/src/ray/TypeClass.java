package ray;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Alexey on 09.09.2016.
 */
@JsonAutoDetect
public enum TypeClass {
    PRIME("Первый класс"),
    BUSINESS("Бизнес класс"),
    ECONOMY("Эконом класс");

    public String s;
    TypeClass(){}
    TypeClass(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }

}
