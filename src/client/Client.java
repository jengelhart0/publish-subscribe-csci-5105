package client;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import communicate.Communicate;
import communicate.CommunicateArticle;
import shared.Message;
import shared.Protocol;

enum RemoteMessageCall {
    JOIN, LEAVE, PUBLISH, SUBSCRIBE, UNSUBSCRIBE
}

public class Client {
    private static final Logger LOGGER = Logger.getLogger( Client.class.getName() );

    private CommunicateArticle communicate = null;
    private String remoteHost;
    private Protocol protocol;

    private Thread listenerThread = null;
    private Thread pingThread = null;
    private ClientListener listener = null;

    private InetAddress localAddress;
    private int listenPort;

    public Client (String remoteHost, Protocol protocol, InetAddress localAddress, int listenPort) {
        this.remoteHost = remoteHost;
        this.protocol = protocol;
        this.localAddress = localAddress;
        this.listenPort = listenPort;
    }

    public boolean Join() {
        return CommunicateWithRemote(RemoteMessageCall.JOIN);
    }

    public boolean Leave() {
        return CommunicateWithRemote(RemoteMessageCall.LEAVE);
    }

    public boolean Publish(Message message) {
        return CommunicateWithRemote(message, RemoteMessageCall.PUBLISH);
    }

    public boolean Subscribe(Message subscription) {
        return CommunicateWithRemote(subscription, RemoteMessageCall.SUBSCRIBE);
    }

    public boolean Unsubscribe(Message subscription) {
        return CommunicateWithRemote(subscription, RemoteMessageCall.UNSUBSCRIBE);
    }

    public boolean Ping() {
        
    }

    private boolean CommunicateWithRemote(RemoteMessageCall call) {
        return CommunicateWithRemote(null, call);
    }

    private boolean CommunicateWithRemote(Message message, RemoteMessageCall call) {
        try {
            if (!remoteCommunicationReady()) {
                establishRemoteCommunication();
            }
            boolean isCallSuccessful = makeCall(message, call);
            if (!isCallSuccessful) {
                throw new RemoteException("Communication attempt returned failure (i.e., false).");
            }
        } catch (RemoteException | NotBoundException | UnknownHostException |
                 IllegalArgumentException | SocketException e) {
            LOGGER.log(Level.SEVERE,
                    "Attempt to establish communication or communicate" + message.asRawMessage() +
                            "failed: " + e.toString());
            return false;
        }
        return true;
    }

    private boolean makeCall(Message message, RemoteMessageCall call)
            throws RemoteException {
        String address = this.localAddress.getHostAddress();
        // TODO: replace this by having communicate calls implement function interface and just pass the function
        if (message == null) {
            switch (call) {
                case JOIN:
                    return communicate.Join(address, this.listenPort);
                case LEAVE:
                    return communicate.Leave(address, this.listenPort);
                default:
                    throw new IllegalArgumentException(
                            "Either Invalid RemoteMessageCall passed or message was null");
            }
        }
        switch (call) {
            case PUBLISH:
                return communicate.Publish(
                        message.asRawMessage(), address, this.listenPort);
            case SUBSCRIBE:
                return communicate.Subscribe(
                        address, this.listenPort, message.asRawMessage());
            case UNSUBSCRIBE:
                return communicate.Unsubscribe(
                        address, this.listenPort, message.asRawMessage());
            default:
                throw new IllegalArgumentException("Invalid RemoteMessageCall passed");
        }
    }

    private boolean remoteCommunicationReady() {
        return (this.communicate != null)
                && (this.listenerThread != null && this.listenerThread.isAlive())
                && (this.localAddress != null)
                && (this.listenPort >= 0);
    }

    private void establishRemoteCommunication()
            throws RemoteException, NotBoundException, UnknownHostException, SocketException {

        establishMessageListener();

        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry = LocateRegistry.getRegistry(this.remoteHost);
        this.communicate = (CommunicateArticle) registry.lookup(CommunicateArticle.NAME);
    }

    private void establishMessageListener()
            throws UnknownHostException, SocketException {

        this.listener = new ClientListener(this.protocol);
        this.listener.listenAt(this.listenPort, this.localAddress);

        this.listenerThread = new Thread(this.listener);
        this.listenerThread.start();
    }

    Set<Message> getCurrentMessageFeed()
            throws IllegalThreadStateException {

        if(this.listenerThread == null || !this.listenerThread.isAlive()) {
            throw new IllegalThreadStateException("Either Listener Thread null or not alive!");
        }
        return this.listener.getCurrentMessageFeed();
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress localAddress = InetAddress.getLocalHost();
        Client testClient = new Client("localhost", CommunicateArticle.ARTICLE_PROTOCOL, localAddress, 8888);
    }

}
