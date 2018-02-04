package server.api;

import shared.Message;
import shared.Protocol;

public interface CommunicationManager {
    boolean Subscribe(Message message);
    boolean Unsubscribe(Message message);
    boolean Publish(Protocol messageProtocol);
    boolean Ping();
}
