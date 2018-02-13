import client.Client;

import java.io.IOException;
import java.rmi.NotBoundException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import communicate.Communicate;
import communicate.CommunicateArticle;
import message.Message;
import message.Protocol;

public class ClientMain {
    private static final Logger LOGGER = Logger.getLogger( ClientMain.class.getName() );
    public static void main(String[] args) throws IOException, NotBoundException {

        if (!(args.length == 1)) {
            LOGGER.log(Level.SEVERE, "Need to pass single argument IPv4 address of server.");
        }

        // public 'server' ip is 73.242.4.186. Testing localhost just to get it up and going.
        String remoteServerIp = args[0];
        LOGGER.log(Level.INFO, remoteServerIp);

        int numTestClients = 1;

        Client[] testClients = new Client[numTestClients];
        int listenPort = 8888;
        for (int i = 0; i < numTestClients; i++) {
            testClients[i] = new Client(
                    remoteServerIp,
                    CommunicateArticle.REMOTE_OBJECT_PORT,
                    Communicate.NAME,
                    CommunicateArticle.ARTICLE_PROTOCOL,
                    listenPort++);

            new Thread(testClients[i]).start();
        }

        Protocol testProtocol = CommunicateArticle.ARTICLE_PROTOCOL;

//        String[] testSubscriptions1 =
//                {"Science;Someone;UMN;", "Sports;Me;Reuters;", "Lifestyle;Jane;YourFavoriteMagazine;",
//                        "Entertainment;Someone;Reuters;", "Business;Jane;The Economist;", "Technology;Jack;Wired;",
//                        "Entertainment;Claus;Reuters;", "Business;Albert;The Economist;", "Business;Albert;Extra;",
//                        ";;The Economist;", "Science;;;", ";Jack;;", "Sports;Me;;", "Lifestyle;;Jane;", "Business;Jack;;"};

        String[] testSubscriptions1 =
                {"Science;Someone;UMN;"};

        for (int i = 0; i < testSubscriptions1.length; i++) {
            testClients[i % testClients.length].subscribe(
                    new Message(testProtocol, testSubscriptions1[i], true));
        }

//        String[] testPublications1 =
//                {"Science;Someone;UMN;content1", "Sports;Me;Reuters;content2", "Lifestyle;Jane;YourFavoriteMagazine;content3",
//                        "Entertainment;Someone;Reuters;content4", "Business;Jane;The Economist;content5", "Technology;Jack;Wired;content6",
//                        "Entertainment;Claus;Reuters;content7", "Business;Albert;The Economist;content8", "Business;Albert;Extra;content9",
//                        ";;The Economist;content10", "Science;;;content11", ";Jack;;content12", "Sports;Me;;content13",
//                        "Lifestyle;;Jane;content14", "Business;Jack;;content15"};

        String[] testPublications1 =
                {"Science;Someone;UMN;content1"};

        for (int i = 0; i < testPublications1.length; i++) {
            testClients[testClients.length - 1 - (i % testClients.length)].publish(
                    new Message(testProtocol, testPublications1[i], false));
        }

        for (Client client: testClients) {
            List<Message> feed = client.getCurrentMessageFeed();
            for (Message message: feed) {
                System.out.println(message.asRawMessage());
            }
            System.out.println("\n");
        }
    }
}
