package client;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import communicate.Communicate;
import shared.Message;
import shared.Protocol;

enum RemoteMessageCall {
    PUBLISH, SUBSCRIBE, UNSUBSCRIBE
}

public class Client {
    private static final Logger LOGGER = Logger.getLogger( Client.class.getName() );

    private Communicate communicate = null;
    private String remoteHost;

    private Thread listenerThread = null;
    private ClientListener listener = null;

    private InetAddress localAddress;
    private int listenPort;

    public Client (String remoteHost, InetAddress localAddress, int listenPort) {
        this.remoteHost = remoteHost;
        this.localAddress = localAddress;
        this.listenPort = listenPort;
    }

    public boolean Publish(Message message) {
        return CommunicateWithRemote(message, RemoteMessageCall.PUBLISH);
    }

    public boolean Subscribe(Message subscription) {
        return CommunicateWithRemote(subscription, RemoteMessageCall.SUBSCRIBE);
    }

    private boolean CommunicateWithRemote(Message message, RemoteMessageCall call) {
        try {
            if (!remoteCommunicationReady()) {
                establishRemoteCommunication(message.getProtocol());
            }

            String address = this.localAddress.getHostAddress();
            boolean isCallSuccessful = false;
            // TODO: replace this by having communicate calls implement function interface and just pass the function
            switch (call) {
                case PUBLISH:
                    isCallSuccessful = communicate.Publish(
                            message.asRawMessage(), address, this.listenPort);
                    break;
                case SUBSCRIBE:
                    isCallSuccessful = communicate.Subscribe(
                            address, this.listenPort, message.asRawMessage());
                    break;
                case UNSUBSCRIBE:
                    isCallSuccessful = communicate.Unsubscribe(
                            address, this.listenPort, message.asRawMessage());
                    break;

                default:
                    throw new IllegalArgumentException("Invalid RemoteMessageCall passed");
            }

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

    private boolean remoteCommunicationReady() {
        return (this.communicate != null)
                && (this.listenerThread != null && this.listenerThread.isAlive())
                && (this.localAddress != null)
                && (this.listenPort >= 0);
    }

    private void establishRemoteCommunication(Protocol protocol)
            throws RemoteException, NotBoundException, UnknownHostException, SocketException {

        establishMessageListener(protocol);

        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        String name = "Communicate";
        Registry registry = LocateRegistry.getRegistry(this.remoteHost);
        this.communicate = (Communicate) registry.lookup(name);
    }

    private void establishMessageListener(Protocol protocol)
            throws UnknownHostException, SocketException {

        this.listener = new ClientListener(protocol);
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
        Client testClient = new Client("localhost", localAddress, 8888);
    }

}
