import communicate.Communicate;
import communicate.CommunicateArticle;
import server.Coordinator;

import java.net.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger LOGGER = Logger.getLogger( Coordinator.class.getName() );
    public static void main(String[] args) throws UnknownHostException {

        // public IP was 73.242.4.186
        // currently just trying to get localhost working

        if (!(args.length == 1)) {
            LOGGER.log(Level.SEVERE, "Need to pass single argument IPv4 address of server.");
        }

        InetAddress serverIp = InetAddress.getByName(args[0]);

        Coordinator coordinator = Coordinator.getInstance();
        coordinator.initialize(
                Communicate.NAME,
                CommunicateArticle.MAXCLIENTS,
                CommunicateArticle.ARTICLE_PROTOCOL,
                serverIp,
                CommunicateArticle.REMOTE_OBJECT_PORT,
                CommunicateArticle.HEARTBEAT_PORT,
                InetAddress.getByName(CommunicateArticle.REGISTRY_SERVER_IP),
                CommunicateArticle.REGISTRY_SERVER_PORT,
                CommunicateArticle.SERVER_LIST_SIZE);
    }

}
