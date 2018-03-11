package server;

import message.Message;
import message.Protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.newCachedThreadPool;

class Dispatcher {
    private static final Logger LOGGER = Logger.getLogger( Dispatcher.class.getName() );

    private Protocol protocol;
    private ExecutorService clientTaskExecutor;

    private Map<String, CommunicationManager> clientToClientManager;

    private boolean shouldRetrieveMatchesAutomatically;

    private MessageStore store;

    Dispatcher(Protocol protocol, MessageStore store, boolean shouldRetrieveMatchesAutomatically) {
        this.protocol = protocol;
        this.store = store;
        this.shouldRetrieveMatchesAutomatically = shouldRetrieveMatchesAutomatically;
        this.clientToClientManager = new ConcurrentHashMap<>();
    }

    void initialize() {
        createClientTaskExecutor();
        if(shouldRetrieveMatchesAutomatically) {
            startSubscriptionPullScheduler();
        }
    }

    void cleanup() {
        clientTaskExecutor.shutdown();
    }

    private void createClientTaskExecutor() {
        this.clientTaskExecutor = newCachedThreadPool();
    }

    private void startSubscriptionPullScheduler() {
        Runnable subscriptionPullScheduler = () -> {
            try {
                while (true) {
                    Thread.sleep(500);
                    for (CommunicationManager manager: clientToClientManager.values()) {
                        queueTaskFor(manager, CommunicationManager.Call.PULL_MATCHES, null);
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.toString());
                e.printStackTrace();
                throw new RuntimeException("Failure in subscription pull scheduler thread.");
            }
        };
        new Thread(subscriptionPullScheduler).start();
    }

    public void addNewClient(String ip, int port) {
        CommunicationManager newClientManager = new ClientManager(ip, port, this.protocol);
        clientToClientManager.put(ServerUtils.getIpPortString(ip, port, protocol), newClientManager);
    }

    public boolean informManagerThatClientLeft(String ip, int port) {
        CommunicationManager whoseClientLeft = clientToClientManager.remove(ServerUtils.getIpPortString(ip, port, protocol));
        if(whoseClientLeft != null) {
            whoseClientLeft.clientLeft();
            return true;
        }
        return false;
     }

    public boolean subscribe(String IP, int Port, String Message) {
        return createMessageTask(IP, Port, Message, CommunicationManager.Call.SUBSCRIBE, true);
    }

    public boolean unsubscribe(String IP, int Port, String Message) {
        return createMessageTask(IP, Port, Message, CommunicationManager.Call.UNSUBSCRIBE, true);
    }

    public boolean publish(String Message, String IP, int Port) {
        return createMessageTask(IP, Port, Message, CommunicationManager.Call.PUBLISH, false);
    }

    private boolean createMessageTask(String ip, int port, String rawMessage,
                                      CommunicationManager.Call call, boolean isSubscription) {

        Message newMessage = createNewMessage(rawMessage, isSubscription);
        if(newMessage == null) {
            return false;
        }
        CommunicationManager manager = getManagerFor(ip, port);
        if(manager == null) {
            LOGGER.log(Level.WARNING, "Client had no manager. May not have joined.");
            return false;
        }
        queueTaskFor(manager, call, newMessage);
        return true;
    }

    private Message createNewMessage(String rawMessage, boolean isSubscription) {
        try {
            return new Message(this.protocol, rawMessage, isSubscription);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid message received from");
            e.printStackTrace();
            return null;
        }
    }

    private CommunicationManager getManagerFor(String ip, int port) {
        return clientToClientManager.get(ServerUtils.getIpPortString(ip, port, protocol));
    }

    private void queueTaskFor(CommunicationManager manager, CommunicationManager.Call call, Message message) {
        this.clientTaskExecutor.execute(manager.task(message, store, call));
    }
}
