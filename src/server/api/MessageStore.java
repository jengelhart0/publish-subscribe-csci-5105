package server.api;

import shared.Message;
import shared.Protocol;

import java.util.HashSet;

public interface MessageStore {
    HashSet<Protocol> match(Message message);
    boolean store(Protocol messageProtocol);
}
