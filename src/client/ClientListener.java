package client;

import shared.Message;
import shared.Protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientListener implements Runnable {

    int

    // TODO: NEED TO EXAMINE WHETHER THERE ARE BETTER SYNCH OPTIONS
    private Set<Message> messageFeed;

    private Protocol protocol;

    private InetAddress localAddress;
    private int listenPort;
    private DatagramSocket listenSocket = null;

    ClientListener(Protocol protocol) {
        this.messageFeed = Collections.synchronizedSet(new HashSet<>());
        this.protocol = protocol;
    }

    void listenAt(int listenPort, InetAddress localAddress) throws SocketException {
        this.listenPort = listenPort;
        this.localAddress = localAddress;
        this.listenSocket = new DatagramSocket(listenPort, localAddress);
    }

    Set<Message> getCurrentMessageFeed() {
        Set<Message> feedCopy = Collections.synchronizedSet(new HashSet<>());
        feedCopy.addAll(this.messageFeed);
        return feedCopy;
    }

    @Override
    public void run() {
        DatagramPacket inPacket = new DatagramPacket(new byte[120])
    }
}
