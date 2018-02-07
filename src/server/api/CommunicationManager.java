package server.api;

import shared.Message;
import shared.Protocol;

public interface CommunicationManager {
    boolean subscribe(Message message);
    boolean unsubscribe(Message message);
    boolean publish(Protocol messageProtocol);
}
