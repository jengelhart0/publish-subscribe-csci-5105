package server.implementation;

import server.api.CommunicationManager;
import shared.Protocol;
import shared.Message;

import java.net.DatagramPacket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

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

    /** Following might be useful? Accidentally started implementing sending through UDP on client side **/
    /*

    public boolean createAndSendMessage(Message message) {
        try {
            message.validate();

            DatagramPacket subscriptionDatagram = createMessagePacket(message);
            sendMessage(subscriptionDatagram);

        } catch (IllegalArgumentException ia) {
            LOGGER.log(Level.SEVERE, ia.toString());
            return false;
        }
        return true;
    }

    public DatagramPacket createMessagePacket(Message message) {
        String rawMessage = message.asRawMessage();

    }

    public boolean sendMessage(DatagramPacket messagePacket) {

    }
    */

}
