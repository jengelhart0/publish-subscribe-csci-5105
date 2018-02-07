package server.implementation;

import communicate.Communicate;
import communicate.CommunicateArticle;
import server.api.CommunicationManager;
import shared.Message;
import shared.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
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

    private ExecutorService clientTaskExecutor;

    private Map<String, CommunicationManager> clientToClientManager;

    private Thread heartbeatThread;
    private HeartbeatListener heartbeatListener;
    private int heartbeatPort;

    private String registerMessage;
    private String deregisterMessage;
    private String getListMessage;

    public static Coordinator getInstance() {
        return ourInstance;
    }

    private Coordinator() {
    }

    void initialize(String name, Protocol protocol, int heartbeatPort) {
        try {
            setCommunicationVariables(name, protocol, heartbeatPort);
            createClientTaskExecutor();
            startHeartbeat();
            makeThisARemoteCommunicationServer();
            registerWithRegistryServer();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to get ip address for server on initialization");

        } finally {
            cleanup();
        }
    }

    private void setCommunicationVariables(String name, Protocol protocol, int heartbeatPort) throws UnknownHostException {
        this.name = name;
        this.protocol = protocol;
        this.ip = InetAddress.getLocalHost();
        this.heartbeatPort = heartbeatPort;


        setRegistryServerMessages();
    }

    private void createClientTaskExecutor() {
        this.clientTaskExecutor = newCachedThreadPool();
    }

    private void startHeartbeat() throws IOException {
        this.heartbeatListener = new HeartbeatListener(this.protocol);
        this.heartbeatListener.listenAt(this.heartbeatPort, this.ip);
        this.heartbeatThread = new Thread(this.heartbeatListener);
        this.heartbeatThread.start();

        if(!this.heartbeatThread.isAlive()) {
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

    private void registerWithRegistryServer() {

    }

    private void deregisterFromRegistryServer() {

    }

    private List<String> getListOfServers() {
        return null;
    }

    @Override
    public boolean Join(String IP, int Port) throws RemoteException {
        ClientManager newClientManager = new ClientManager(IP, Port);
        clientToClientManager.put(getIpPortString(IP, Port), newClientManager);
        return true;
    }

    private String getIpPortString(String ip, int port) {
        return ip + Integer.toString(port);
    }

    @Override
    public boolean Leave(String IP, int Port) throws RemoteException {
        return false;
    }

    @Override
    public boolean Subscribe(String IP, int Port, String Article) throws RemoteException {
        return false;
    }

    @Override
    public boolean Unsubscribe(String IP, int Port, String Article) throws RemoteException {
        return false;
    }

    @Override
    public boolean Publish(String Article, String IP, int Port) throws RemoteException {

        ClientManager test = new ClientManager(IP, Port);
        Message newMessage = new Message(this.protocol, Article, false);

        this.clientTaskExecutor.execute(test.task(newMessage, CommunicationManager.Call.PUBLISH));
        return false;
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
        return false;
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

    public static void main(String[] args) {
        Coordinator coordinator = Coordinator.getInstance();
        coordinator.initialize(CommunicateArticle.NAME,
                CommunicateArticle.ARTICLE_PROTOCOL,
                8888);
    }

}
