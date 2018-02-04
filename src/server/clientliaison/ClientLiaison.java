package server.clientliaison;

import server.api.CommunicationManager;
import server.api.Protocol;
import server.api.Message;

public class ClientLiaison implements CommunicationManager {
    @Override
    public boolean Subscribe(Message message) {
        return false;
    }

    @Override
    public boolean Unsubscribe(Message message) {
        return false;
    }

    @Override
    public boolean Publish(Protocol messageProtocol) {
        return false;
    }

    @Override
    public boolean Ping() {
        return false;
    }
}
