package server.implementation;

import server.api.CommunicationManager;
import server.api.MessageStore;
import shared.Message;

import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientManager implements CommunicationManager {
    private static final Logger LOGGER = Logger.getLogger( ClientManager.class.getName() );

    private String clientIp;
    private int clientPort;

    private boolean clientLeft;

    private List<Message> subscriptions;
    private List<Message> publications;

    private final Object subscriptionLock = new Object();
    private final Object publicationLock = new Object();

    private static final MessageStore store = TripleKeyValueStore.getInstance();

    ClientManager(String clientIp, int clientPort) {
        this.clientIp = clientIp;
        this.clientPort = clientPort;

        this.clientLeft = false;

        this.subscriptions = Collections.synchronizedList(new LinkedList<>());
        this.publications = Collections.synchronizedList(new ArrayList<>());

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
        this.subscriptions.add(message);
    }

    @Override
    public void unsubscribe(Message message) {

    }

    @Override
    public void publish(Message message) {
        this.publications.add(message);
        store.publish(message);
    }

    // TODO: Gracefully just return if client has called Leave() (could happen if a pull task is still on executor after Leave()).
    // TODO: Go ahead and let any other task just finish up in such a circumstance.

    @Override
    public void pullSubscriptionMatches() {
        if(!clientLeft) {

        }
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

    public void clientLeft() {
        this.clientLeft = true;
    }
}
