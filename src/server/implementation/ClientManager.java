package server.implementation;

import server.api.CommunicationManager;
import server.api.MessageStore;
import shared.Message;

import java.net.DatagramPacket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientManager implements CommunicationManager {
    private static final Logger LOGGER = Logger.getLogger( ClientManager.class.getName() );


    private String clientIp;
    private int clientPort;

    private Set<Message> subscriptions;
    private Set<Message> publications;

    private static final MessageStore store = TripleKeyValueStore.getInstance();

    public ClientManager(String clientIp, int clientPort) {
        this.clientIp = clientIp;
        this.clientPort = clientPort;

        this.subscriptions = new HashSet<>();
        this.publications = new HashSet<>();

    }

    public Runnable task(Message message, CommunicationManager.Call call) {
        switch(call) {
            case SUBSCRIBE:
                return () -> subscribe(message);
            case PUBLISH:
                return () -> publish(message);
            case UNSUBSCRIBE:
                return () -> unsubscribe(message);
            case PULL_MATCHES:
                return this::pullSubscriptionMatches;
            default:
                throw new IllegalArgumentException("Task call made to ClientManager not recognized.");
        }
    }

    @Override
    public void subscribe(Message message) {

    }

    @Override
    public void unsubscribe(Message message) {

    }

    @Override
    public void publish(Message message) {
        store.publish(message);
    }

    // TODO: Gracefully just return if client has called Leave() (could happen if a pull task is still on executor after Leave()).
    // TODO: Go ahead an let any other task just finish up in such a circumstance.

    @Override
    public void pullSubscriptionMatches() {
    }

    private boolean createAndSendMessage(Message message) {
        try {

            DatagramPacket subscriptionDatagram = createMessagePacket(message);
            sendMessage(subscriptionDatagram);

        } catch (IllegalArgumentException ia) {
            LOGGER.log(Level.SEVERE, ia.toString());
            return false;
        }
        return true;
    }

    private DatagramPacket createMessagePacket(Message message) {
        String rawMessage = message.asRawMessage();

        //TODO:implement

        return null;
    }

    private boolean sendMessage(DatagramPacket messagePacket) {
        return false;
    }

}
