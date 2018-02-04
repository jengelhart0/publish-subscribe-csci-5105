package server.api;

import java.util.HashSet;

public interface MessageStore {
    HashSet<Protocol> match(Message message);
    boolean store(Protocol messageProtocol);
}
