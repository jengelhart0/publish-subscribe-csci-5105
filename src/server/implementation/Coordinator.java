package server.implementation;

import communicate.Communicate;
import communicate.CommunicateArticle;
import server.api.CommunicationManager;
import shared.Protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coordinator implements Communicate {
    private static final Logger LOGGER = Logger.getLogger( Coordinator.class.getName() );

    private String name;
    private Protocol protocol;
    private int heartbeatPort;
    private static Coordinator ourInstance = new Coordinator();
    private Map<String, CommunicationManager> clientToClientManager;

    private String registerMessage;
    private String deregisterMessage;
    private String getListMessage;

    public static Coordinator getInstance() {
        return ourInstance;
    }

    private Coordinator() {
    }

    private void initialize(String name, Protocol protocol, int heartbeatPort) {
        setCommunicationVariables(name, protocol, heartbeatPort);
        startHeartbeat();
        createRmiExecutor();
        registerWithRegistryServer();
    }

    private void setCommunicationVariables(String name, Protocol protocol, int heartbeatPort) {
        this.name = name;
        this.protocol = protocol;
        this.heartbeatPort = heartbeatPort;

        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            // TODO: Figure out what Port vs. RMI Port means...
            this.registerMessage = "Register;RMI;" + ip + ";" + heartbeatPort + ";" + name + "!!!!!";
            this.deregisterMessage = "Deregister;RMI;" + ip + ";" + heartbeatPort;
            this.getListMessage = "GetList;RMI;" + ip + ";" + heartbeatPort;
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, "Failed to get ip address for server on initialization");
        }
    }

    private void startHeartbeat() {

    }

    private void createRmiExecutor() {

    }

    private void registerWithRegistryServer() {

    }

    private void deregisterFromRegistryServer() {

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

    private List<String> getListOfServers() {
        return null;
    }

    @Override
    public boolean Join(String IP, int Port) throws RemoteException {
        return false;
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
