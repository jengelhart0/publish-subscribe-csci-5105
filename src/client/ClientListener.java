package client;

import shared.Message;
import shared.Protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientListener implements Runnable {

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
        int messageSize = this.protocol.getMessageSize();
        while(true) {
            Message newMessage = getMessageFromRemote(messageSize);
            this.messageFeed.add(newMessage);
        }
    }

    private Message getMessageFromRemote(int messageSize) throws IOException {
        DatagramPacket packetFromRemote = new DatagramPacket(new byte[messageSize], messageSize);

        this.listenSocket.receive(packetFromRemote);

        DataInputStream inputStream = new DataInputStream(
                new ByteArrayInputStream(
                        packetFromRemote.getData()
                )
        );
        byte[] newMessageData = new byte[messageSize];
        inputStream.read(newMessageData);
        Message newMessage = new Message(this.protocol, new String(newMessageData, "UTF8"));

    }
}
