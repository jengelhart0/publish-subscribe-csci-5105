package server.implementation;

import communicate.Communicate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coordinator implements Communicate {
    private static final Logger LOGGER = Logger.getLogger( Coordinator.class.getName() );

    private static Coordinator ourInstance = new Coordinator();

    public static Coordinator getInstance() {
        return ourInstance;
    }

    private Coordinator() {
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

    public static void main(String[] args) {
        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Communicate";
            Communicate coordinator = Coordinator.getInstance();
            Communicate stub =
                    (Communicate) UnicastRemoteObject.exportObject(coordinator, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            LOGGER.log(Level.FINE, "Coordinator bound");
        } catch (RemoteException re) {
            LOGGER.log(Level.SEVERE, re.toString());
        }
    }

}
