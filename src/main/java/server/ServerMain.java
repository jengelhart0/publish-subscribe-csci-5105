import communicate.Communicate;
import communicate.CommunicateArticle;
import server.Coordinator;

import java.io.IOException;
import java.net.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger LOGGER = Logger.getLogger( Coordinator.class.getName() );

    private static Coordinator startServer() throws IOException {
        Coordinator coordinator = Coordinator.getInstance();
        coordinator.initialize(
                Communicate.NAME,
                CommunicateArticle.MAXCLIENTS,
                CommunicateArticle.ARTICLE_PROTOCOL,
                InetAddress.getByName("127.0.0.1"),
                CommunicateArticle.REMOTE_OBJECT_PORT,
                CommunicateArticle.HEARTBEAT_PORT,
                CommunicateArticle.GET_LIST_PORT,
                InetAddress.getByName(CommunicateArticle.REGISTRY_SERVER_IP),
                CommunicateArticle.REGISTRY_SERVER_PORT,
                CommunicateArticle.SERVER_LIST_SIZE);
        return coordinator;
    }

    private static boolean teardownTest() throws IOException, InterruptedException {
        Coordinator server = startServer();
        Thread.sleep(10000);
        server.cleanup();
        return true;
    }

    private static String[] getListTest() throws IOException, InterruptedException {
        Coordinator server = startServer();
        Thread.sleep(3000);
        String[] serverList = server.getListOfServers();
        if (serverList != null) {
            System.out.print("Server List: ");
            for (String serverString: serverList) {
                System.out.print(serverString + " ");
            }
        }
        return serverList;
    }

    public static void main(String[] args) throws IOException, InterruptedException{

        // public IP was 73.242.4.186
        // currently just trying to get localhost working

        if (args.length == 1) {
            String registryServerTest = args[0];
            switch (registryServerTest) {
                case "testTeardown":
                    teardownTest();
                    break;
                case "testGetList":
                    getListTest();
                    break;
            }

        } else {
            if (!(args.length > 1)) {
                LOGGER.log(Level.SEVERE, "Usage: java ServerMain <test name>");
            }

            // We must always run server locally to this codebase, so...
//            InetAddress serverIp = InetAddress.getByName(args[0]);
//            InetAddress serverIp = InetAddress.getByName("127.0.0.1");
            startServer();
        }
    }
}
