package server;

import message.Message;
import message.Protocol;

import java.util.Set;

public interface MessageStore {
    Set<String> retrieve(Message subscription);
    boolean publish(Message message);
    Query generateQuery(Message message, Protocol protocol);
}
