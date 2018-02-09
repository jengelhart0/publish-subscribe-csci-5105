package client;

import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import communicate.Communicate;
import communicate.CommunicateArticle;
import communicate.Communicate.RemoteMessageCall;
import shared.Message;
import shared.Protocol;

public class Client implements Runnable {
    private static final Logger LOGGER = Logger.getLogger( Client.class.getName() );

    private Communicate communicate = null;
    private String communicateName;
    private String remoteHost;
    private Protocol protocol;

    private Thread listenerThread = null;
    private ClientListener listener = null;

    private InetAddress localAddress;
    private int listenPort;

    public Client (String remoteHost, String communicateName, Protocol protocol,
                   int listenPort) throws UnknownHostException {
        this.remoteHost = remoteHost;
        this.communicateName = communicateName;
        this.protocol = protocol;
        this.localAddress = InetAddress.getLocalHost();
        this.listenPort = listenPort;
    }

    public boolean join() {
        return communicateWithRemote(RemoteMessageCall.JOIN);
    }

    public boolean leave() {
        return communicateWithRemote(RemoteMessageCall.LEAVE);
    }

    public boolean publish(Message message) {
        return communicateWithRemote(message, RemoteMessageCall.PUBLISH);
    }

    public boolean subscribe(Message subscription) {
        return communicateWithRemote(subscription, RemoteMessageCall.SUBSCRIBE);
    }

    public boolean unsubscribe(Message subscription) {
        return communicateWithRemote(subscription, RemoteMessageCall.UNSUBSCRIBE);
    }

    private boolean ping() throws RemoteException {
        return this.communicate.Ping();
    }

    private boolean communicateWithRemote(RemoteMessageCall call) {
        return communicateWithRemote(null, call);
    }

    private boolean communicateWithRemote(Message message, RemoteMessageCall call) {
        try {
            boolean isCallSuccessful = makeCall(message, call);
            if (!isCallSuccessful) {
                throw new RemoteException("Communication attempt returned failure (i.e., false).");
            }
        } catch (RemoteException | IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE,
                    "Attempt to establish communication or communicate" + message.asRawMessage() +
                            "failed: " + e.toString());
            return false;
        }
        return true;
    }

    private boolean makeCall(Message message, RemoteMessageCall call)
            throws RemoteException {

        if (this.communicate == null) {
            throw new IllegalArgumentException(
                    "It appears client's remote communication is unestablished. " +
                    "Ensure you called start() on client thread."
            );
        }

        String address = this.localAddress.getHostAddress();
        // TODO: replace this by having communicate calls implement function interface and just pass the function
        if (message == null) {
            switch (call) {
                case JOIN:
                    return this.communicate.Join(address, this.listenPort);
                case LEAVE:
                    return this.communicate.Leave(address, this.listenPort);
                default:
                    throw new IllegalArgumentException(
                            "Either Invalid RemoteMessageCall passed or message was null");
            }
        }
        switch (call) {
            case PUBLISH:
                return this.communicate.Publish(
                        message.asRawMessage(), address, this.listenPort);
            case SUBSCRIBE:
                return this.communicate.Subscribe(
                        address, this.listenPort, message.asRawMessage());
            case UNSUBSCRIBE:
                return this.communicate.Unsubscribe(
                        address, this.listenPort, message.asRawMessage());
            default:
                throw new IllegalArgumentException("Invalid RemoteMessageCall passed");
        }
    }

    @Override
    public void run() {
        try {
            initializeRemoteCommunication();
            while(true) {
                ping();
                Thread.sleep(10000);
            }
        } catch (RemoteException | NotBoundException | InterruptedException | SocketException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        } finally {
            cleanup();
        }
    }

    private void initializeRemoteCommunication() throws RemoteException, NotBoundException, SocketException {
        startMessageListener();
        establishSecurityManager();
        establishRemoteObject();
        join();
    }

    private void startMessageListener() throws SocketException {
        this.listener = new ClientListener(this.protocol);
        this.listener.listenAt(this.listenPort, this.localAddress);
        this.listenerThread = new Thread(this.listener);
        this.listenerThread.start();

        if(!this.listenerThread.isAlive()) {
            throw new RuntimeException();
        }


    }

    private void establishSecurityManager() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    }

    private void establishRemoteObject() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(this.remoteHost);
        this.communicate = (Communicate) registry.lookup(this.communicateName);
    }

    private void cleanup() {
        leave();
        this.listener.tellThreadToStop();
    }

    List<Message> getCurrentMessageFeed()
            throws IllegalThreadStateException {

        if(this.listenerThread == null || !this.listenerThread.isAlive()) {
            throw new IllegalThreadStateException("Either Listener Thread null or not alive!");
        }
        return this.listener.getCurrentMessageFeed();
    }

    public static void main(String[] args) throws UnknownHostException {
        Client testClient = new Client(
                "localhost",
                CommunicateArticle.NAME,
                CommunicateArticle.ARTICLE_PROTOCOL,
                8888);

        new Thread(testClient).start();
    }

}
