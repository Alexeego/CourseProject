package frames;

import connection.MessageType;

/**
 * Created by Alexey on 01.10.2016.
 */
public interface InfoListener {
    void event(MessageType type, Object object);
}
