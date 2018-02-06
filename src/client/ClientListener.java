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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientListener implements Runnable {
    private static final Logger LOGGER = Logger.getLogger( ClientListener.class.getName() );

    // TODO: NEED TO EXAMINE WHETHER THERE ARE BETTER SYNCH OPTIONS
    private Set<Message> messageFeed;

    private Protocol protocol;
    private DatagramSocket listenSocket = null;

    ClientListener(Protocol protocol) {
        this.messageFeed = Collections.synchronizedSet(new HashSet<>());
        this.protocol = protocol;
    }

    void listenAt(int listenPort, InetAddress localAddress) throws SocketException {
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

        byte[] messageBuffer = new byte[messageSize];
        DatagramPacket packetToReceive = new DatagramPacket(new byte[messageSize], messageSize);

        while(true) {
            try {
                Message newMessage = getMessageFromRemote(messageBuffer, packetToReceive);
                this.messageFeed.add(newMessage);
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "ClientListener failed to receive incoming message: " + e.toString());
            }
        }
    }

    private Message getMessageFromRemote(byte[] messageBuffer, DatagramPacket packetToReceive) throws IOException {
        this.listenSocket.receive(packetToReceive);

        try (DataInputStream inputStream = new DataInputStream(
                new ByteArrayInputStream(packetToReceive.getData()))) {
            inputStream.read(messageBuffer);
        }
        return new Message(this.protocol, new String(messageBuffer, "UTF8"));
    }
}
