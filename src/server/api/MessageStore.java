package server.api;

import server.implementation.Query;
import shared.Message;
import shared.Protocol;

import java.util.Set;

public interface MessageStore {
    Set<String> retrieve(Message subscription);
    boolean publish(Message message);
    Query generateQuery(Message message, Protocol protocol);
}
