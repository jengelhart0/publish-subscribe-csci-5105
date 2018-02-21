package server.implementation;

import communicate.Communicate;
import communicate.CommunicateArticle;
import server.api.CommunicationManager;
import Message.Message;
import Message.Protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class Coordinator implements Communicate {
    private static final Logger LOGGER = Logger.getLogger( Coordinator.class.getName() );

    private static Coordinator ourInstance = new Coordinator();

    private String name;
    private Protocol protocol;
    private InetAddress ip;

    private int maxClients;
    private int numClients;

    private static final Object numClientsLock = new Object();

    private ExecutorService clientTaskExecutor;

    private Map<String, CommunicationManager> clientToClientManager;

    private HeartbeatListener heartbeatListener;
    private int heartbeatPort;

    private InetAddress registryServerAddress;
    private int registryServerPort;
    private int serverListSize;

    private String registerMessage;
    private String deregisterMessage;
    private String getListMessage;

    private static Coordinator getInstance() {
        return ourInstance;
    }

    private Coordinator() {
    }

    private void initialize(String name, int maxClients, Protocol protocol, int heartbeatPort,
                    InetAddress registryServerIp, int registryServerPort, int serverListSize) {
        try {
            setCommunicationVariables(name, maxClients, protocol, heartbeatPort,
                    registryServerIp, registryServerPort, serverListSize);
            createClientTaskExecutor();
            startHeartbeat();
            makeThisARemoteCommunicationServer();
            registerWithRegistryServer();
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Failed on server initialization: " + e.toString());
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            heartbeatListener.tellThreadToStop();
            deregisterFromRegistryServer();
            clientTaskExecutor.shutdown();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "While cleaning up server: " + e.toString());
        }
    }

    private void setCommunicationVariables(String name, int maxClients, Protocol protocol, int heartbeatPort,
                                           InetAddress registryServerIp, int registryServerPort, int serverListSize)
            throws UnknownHostException {

        this.name = name;
        this.protocol = protocol;

        this.maxClients = maxClients;
        this.numClients = 0;

        this.clientToClientManager = new ConcurrentHashMap<>();

        this.ip = InetAddress.getLocalHost();
        this.heartbeatPort = heartbeatPort;

        this.registryServerAddress = registryServerIp;
        this.registryServerPort = registryServerPort;
        this.serverListSize = serverListSize;

        setRegistryServerMessages();
    }

    private void createClientTaskExecutor() {
        this.clientTaskExecutor = newCachedThreadPool();
    }

    private void startHeartbeat() throws IOException {
        this.heartbeatListener = new HeartbeatListener(this.protocol);
        this.heartbeatListener.listenAt(this.heartbeatPort, this.ip);
        Thread heartbeatThread = new Thread(this.heartbeatListener);
        heartbeatThread.start();

        if(!heartbeatThread.isAlive()) {
            throw new RuntimeException();
        }
    }

    private void makeThisARemoteCommunicationServer() {
        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Communicate stub =
                    (Communicate) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(this.name, stub);
            LOGGER.log(Level.FINE, "Coordinator bound");
        } catch (RemoteException re) {
            LOGGER.log(Level.SEVERE, re.toString());
        }
    }

    private void setRegistryServerMessages() throws UnknownHostException {
        String ip = this.ip.getHostAddress();

        // TODO: Figure out what Port vs. RMI Port means...
        this.registerMessage = "Register;RMI;" + ip + ";" + heartbeatPort + ";" + name + "!!!!!";
        this.deregisterMessage = "Deregister;RMI;" + ip + ";" + heartbeatPort;
        this.getListMessage = "GetList;RMI;" + ip + ";" + heartbeatPort;
    }

    private void registerWithRegistryServer() throws IOException {
        sendRegistryServerMessage(this.registerMessage);
    }

    private void deregisterFromRegistryServer() throws IOException {
        sendRegistryServerMessage(this.deregisterMessage);
    }

    private List<String> getListOfServers() throws IOException {
        int listSizeinBytes = this.serverListSize;
        DatagramPacket registryPacket = new DatagramPacket(new byte[listSizeinBytes], listSizeinBytes);
        byte[] serversBuffer = new byte[listSizeinBytes];

        try (DatagramSocket getListSocket = new DatagramSocket()) {
            // sending here to minimize chance response arrives before we listen for it
            sendRegistryServerMessage(this.getListMessage);
            getListSocket.receive(registryPacket);
        }

        try (DataInputStream inputStream = new DataInputStream(
                new ByteArrayInputStream(registryPacket.getData()))) {
            inputStream.read(serversBuffer);
        }

        String servers = new String(serversBuffer, "UTF8");

        LOGGER.log(Level.INFO, servers);
        // TODO: Need to figure out what the format of this is: handout not clear, check the log result of it.
        System.exit(0);
        return null;
    }

    private void sendRegistryServerMessage(String rawMessage) throws IOException {
        DatagramPacket packet = makeRegistryServerPacket();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
        }
    }

    private DatagramPacket makeRegistryServerPacket() {
        int messageSize = this.protocol.getMessageSize();
        return new DatagramPacket(new byte[messageSize], messageSize, this.registryServerAddress, registryServerPort);
    }

    @Override
    public boolean Join(String IP, int Port) throws RemoteException {
        synchronized (numClientsLock) {
            if(numClients >= maxClients) {
                return false;
            }
            numClients++;
        }
        CommunicationManager newClientManager = new ClientManager(IP, Port, this.protocol);
        clientToClientManager.put(getIpPortString(IP, Port), newClientManager);
        return true;
    }

    @Override
    public boolean Leave(String IP, int Port) throws RemoteException {
        CommunicationManager removed = clientToClientManager.remove(getIpPortString(IP, Port));
        // Only decrement num clients if a non-null manager was associated with client
        if(removed != null) {
            synchronized (numClientsLock) {
                numClients--;
            }
            removed.informManagerThatClientLeft();
        }
        return true;
    }

    @Override
    public boolean Subscribe(String IP, int Port, String Article) throws RemoteException {
        return createMessageTask(IP, Port, Article, CommunicationManager.Call.SUBSCRIBE, true);
    }

    @Override
    public boolean Unsubscribe(String IP, int Port, String Article) throws RemoteException {
        return createMessageTask(IP, Port, Article, CommunicationManager.Call.UNSUBSCRIBE, true);
    }

    @Override
    public boolean Publish(String Article, String IP, int Port) throws RemoteException {
        return createMessageTask(IP, Port, Article, CommunicationManager.Call.PUBLISH, false);
    }

    @Override
    public boolean JoinServer(String IP, int Port) throws RemoteException {
        // TODO: optional
        throw new RemoteException("JoinServer not implemented!");
    }

    @Override
    public boolean LeaveServer(String IP, int Port) throws RemoteException {
        // TODO: optional
        throw new RemoteException("LeaveServer not implemented!");
    }

    @Override
    public boolean PublishServer(String Article, String IP, int Port) throws RemoteException {
        // TODO: optional
        throw new RemoteException("PublishServer not implemented!;");
    }

    @Override
    public boolean Ping() throws RemoteException {
        return true;
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
            return null;
        }
    }

    private CommunicationManager getManagerFor(String ip, int port) {
        return clientToClientManager.get(getIpPortString(ip, port));
    }

    private void queueTaskFor(CommunicationManager manager, CommunicationManager.Call call, Message message) {
        this.clientTaskExecutor.execute(manager.task(message, call));
    }

    private String getIpPortString(String ip, int port) {
        return ip + this.protocol.getDelimiter() + Integer.toString(port);
    }

    void setRegisterMessage(String registerMessage) {
        this.registerMessage = registerMessage;
    }

    void setDeregisterMessage(String deregisterMessage) {
        this.deregisterMessage = deregisterMessage;
    }

    void setGetListMessage(String getListMessage) {
        this.getListMessage = getListMessage;
    }

    public static void main(String[] args) throws UnknownHostException {
        Coordinator coordinator = Coordinator.getInstance();
        coordinator.initialize(
                CommunicateArticle.NAME,
                CommunicateArticle.MAXCLIENTS,
                CommunicateArticle.ARTICLE_PROTOCOL,
                CommunicateArticle.HEARTBEAT_PORT,
                InetAddress.getByName(CommunicateArticle.REGISTRY_SERVER_IP),
                CommunicateArticle.REGISTRY_SERVER_PORT,
                CommunicateArticle.SERVER_LIST_SIZE);
    }
}
