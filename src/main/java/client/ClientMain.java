import client.Client;

import java.io.IOException;
import java.rmi.NotBoundException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import communicate.Communicate;
import communicate.CommunicateArticle;
import message.Message;
import message.Protocol;

//        String[] testPublications1 =
//                {"Science;Someone;UMN;content1", "Sports;Me;Reuters;content2", "Lifestyle;Jane;YourFavoriteMagazine;content3",
//                "Entertainment;Someone;Reuters;content4", "Business;Jane;The Economist;content5", "Technology;Jack;Wired;content6",
//                "Entertainment;Claus;Reuters;content7", "Business;Albert;The Economist;content8", "Business;Albert;Extra;content9",
//                ";;The Economist;content10", "Science;;;content11", ";Jack;;content12", "Sports;Me;;content13",
//                "Lifestyle;;Jane;content14", "Business;Jack;;content15"};
//
//                String[] testSubscriptions1 =
//                {"Science;Someone;UMN;", "Sports;Me;Reuters;", "Lifestyle;Jane;YourFavoriteMagazine;",
//                "Entertainment;Someone;Reuters;", "Business;Jane;The Economist;", "Technology;Jack;Wired;",
//                "Entertainment;Claus;Reuters;", "Business;Albert;The Economist;", "Business;Albert;Extra;",
//                ";;The Economist;", "Science;;;", ";Jack;;", "Sports;Me;;", "Lifestyle;;Jane;", "Business;Jack;;"};


//        for (int i = 0; i < testSubscriptions1.length; i++) {
//                testClients[i % testClients.length].subscribe(
//                new Message(testProtocol, testSubscriptions1[i], true));
//                }
//
//
//
//                String[] testPublications1 =
//                {"Science;Someone;UMN;content1"};
//
//                for (int i = 0; i < testPublications1.length; i++) {
//                testClients[testClients.length - 1 - (i % testClients.length)].publish(
//                new Message(testProtocol, testPublications1[i], false));
//                }
//
//                for (Client client : testClients) {
//                List<Message> feed = client.getCurrentMessageFeed();
//                for (Message message : feed) {
//                System.out.println(message.asRawMessage());
//                }
//                System.out.println("\n");
//                }





public class ClientMain {
    private static final Logger LOGGER = Logger.getLogger( ClientMain.class.getName() );

    private static Client createNewClient(String remoteServerIp, int listenPort) throws IOException, NotBoundException {
        Client client = new Client(
                remoteServerIp,
                CommunicateArticle.REMOTE_OBJECT_PORT,
                Communicate.NAME,
                CommunicateArticle.ARTICLE_PROTOCOL,
                listenPort++);

        new Thread(client).start();
        return client;
    }

    private static boolean validateReceivedMessages(List<Message> received, String[] expected, Protocol protocol) {

        Set<String> receivedMessages = new HashSet<>();

        for (Message message : received) {
            receivedMessages.add(message.asRawMessage());
        }

        Set<String> expectedSet = new HashSet<>();

        for (String expectedString: expected) {
            expectedSet.add(protocol.padMessage(expectedString));
        }


        int expectedSize = expectedSet.size();
        int receivedSize = receivedMessages.size();

        expectedSet.retainAll(receivedMessages);

        int intersectSize = expectedSet.size();

        return (expectedSize == receivedSize) && (receivedSize == intersectSize);
    }

    private static void runAllTests(String remoteServerIp, Protocol protocol) throws  IOException, NotBoundException, InterruptedException {
        String[] publications1 =
                {"Science;Someone;UMN;content1", "Sports;Me;Reuters;content2", "Lifestyle;Jane;YourFavoriteMagazine;content3",
                        "Entertainment;Someone;Reuters;content4", "Business;Jane;The Economist;content5", "Technology;Jack;Wired;content6",
                        "Entertainment;Claus;Reuters;content7", "Business;Albert;The Economist;content8", "Business;Albert;Extra;content9",
                        ";;The Economist;content10", "Science;;;content11", ";Jack;;content12", "Sports;Me;;content13",
                        "Lifestyle;;Jane;content14", "Business;Jack;;content15"};

        String[] subscriptions1 =
                {"Science;Someone;UMN;", "Sports;Me;Reuters;", "Lifestyle;Jane;YourFavoriteMagazine;",
                        "Entertainment;Someone;Reuters;", "Business;Jane;The Economist;"};

        String[] expected1 =
                {"Science;Someone;UMN;content1", "Sports;Me;Reuters;content2", "Lifestyle;Jane;YourFavoriteMagazine;content3",
                        "Entertainment;Someone;Reuters;content4", "Business;Jane;The Economist;content5",};



        String resultMessage;
        if(testSingleSubscriberSinglePublisher(remoteServerIp, protocol, publications1, subscriptions1, expected1)) {
            resultMessage = "Test single subscriber, single publisher PASSED.";
        } else {
            resultMessage = "Test single subscriber, single publisher FAILED.";
        }
        LOGGER.log(Level.INFO, resultMessage);

        if(testSinglePublishMultipleSubscribers(remoteServerIp, 10, protocol, publications1, subscriptions1, expected1)) {
            resultMessage = "Test single publisher, multiple subscribers PASSED.";
        } else {
            resultMessage = "Test single publisher, multiple subscribers FAILED.";
        }

        LOGGER.log(Level.INFO, resultMessage);
    }

    private static void addSubscriptions(String[] subscriptions, Protocol protocol, Client client)
            throws  IOException, NotBoundException {

        for (String subscription : subscriptions) {
            client.subscribe(new Message(protocol, subscription, true));
        }
    }

    private static void makePublications(String[] publications, Protocol protocol, Client client)
            throws  IOException, NotBoundException {

        for (String publication : publications) {
            client.publish(new Message(protocol, publication, false));
        }
    }

    private static boolean testSingleSubscriberSinglePublisher(String remoteServerIp, Protocol protocol, String[] publications,
            String[] subscriptions, String[] expected) throws IOException, NotBoundException, InterruptedException {

        int listenPort = 8888;

        Client subscriber = createNewClient(remoteServerIp, listenPort++);
        addSubscriptions(subscriptions, protocol, subscriber);

        Client publisher = createNewClient(remoteServerIp, listenPort);
        makePublications(publications, protocol, publisher);

        Thread.sleep(6000);

        List<Message> receivedMessages = subscriber.getCurrentMessageFeed();

        boolean passed = validateReceivedMessages(receivedMessages, expected, protocol);

        subscriber.terminateClient();
        publisher.terminateClient();

        return passed;
    }

    private static boolean testSinglePublishMultipleSubscribers(String remoteServerIp, int numSubscribers, Protocol protocol,
            String[] publications, String[] subscriptions, String[] expected) throws IOException, NotBoundException, InterruptedException {

        int listenPort = 9888;

        List<Client> subscribers = new LinkedList<>();

        for (int i = 0; i < numSubscribers; i++) {
            Client newSubscriber = createNewClient(remoteServerIp, listenPort++);
            subscribers.add(newSubscriber);
            addSubscriptions(subscriptions, protocol, newSubscriber);
        }

        Client publisher = createNewClient(remoteServerIp, listenPort);
        makePublications(publications, protocol, publisher);
        publisher.terminateClient();

        Thread.sleep(6000);

        boolean allPassed = true;
        for (Client subscriber: subscribers) {
            if (!validateReceivedMessages(subscriber.getCurrentMessageFeed(), expected, protocol)) {
                allPassed = false;
                subscriber.terminateClient();
                break;
            }
            subscriber.terminateClient();
        }
        return allPassed;
    }

    private static boolean testPublishInvalid() {
        return false;
    }

    private static boolean testSubscribeInvalid() {
        return false;
    }

    public static boolean testUnsubscribe() {
        return false;
    }

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {

        if (!(args.length == 1)) {
            LOGGER.log(Level.SEVERE, "Need to pass single argument IPv4 address of server.");
        }

        // public 'server' ip is 73.242.4.186. Testing localhost just to get it up and going.
        String remoteServerIp = args[0];
        LOGGER.log(Level.INFO, remoteServerIp);

        runAllTests(remoteServerIp, CommunicateArticle.ARTICLE_PROTOCOL);
    }
}
