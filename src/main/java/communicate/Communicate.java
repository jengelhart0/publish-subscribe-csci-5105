package communicate;

import server.ReplicatedPubSubServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Communicate extends Remote {
    String NAME = "Communicate";

    enum RemoteMessageCall {
        JOIN, LEAVE, PUBLISH, SUBSCRIBE, UNSUBSCRIBE
    }

    boolean Join(String IP, int Port) throws RemoteException;
    boolean Leave(String IP, int Port) throws RemoteException;
    boolean Subscribe(String IP, int Port, String Message) throws RemoteException;
    boolean Unsubscribe(String IP, int Port, String Message) throws RemoteException;
    boolean Publish(String Message, String IP, int Port) throws RemoteException;
    ReplicatedPubSubServer getCoordinator() throws RemoteException;
    boolean Ping() throws RemoteException;
}
