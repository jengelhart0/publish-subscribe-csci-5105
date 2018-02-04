package server.implementation;

import server.api.CommunicationManager;
import shared.Protocol;
import shared.Message;

import java.util.HashSet;
import java.util.Set;

public class ClientLiaison implements CommunicationManager {

    String clientIp;
    String clientPort;

    Set<Message> subscriptions;
    Set<Message> publications;

    public ClientLiaison(String clientIp, String clientPort) {
        this.clientIp = clientIp;
        this.clientPort = clientPort;

        this.subscriptions = new HashSet<>();
        this.publications = new HashSet<>();
    }

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
