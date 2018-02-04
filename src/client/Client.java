package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import communicate.Communicate;

public class Client {
    private static final Logger LOGGER = Logger.getLogger( Client.class.getName() );

    Communicate communicate;

    public Client (String remoteHost) {

        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String name = "Communicate";
            Registry registry = LocateRegistry.getRegistry(remoteHost);
            this.communicate = (Communicate) registry.lookup(name);

        } catch (RemoteException | NotBoundException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }

    public static void main(String[] args) {
        Client testClient = new Client("localhost");
    }

}
