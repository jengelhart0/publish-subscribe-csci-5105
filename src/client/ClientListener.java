package client;

import listener.Listener;
import shared.Message;
import shared.Protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientListener extends Listener {
    private static final Logger LOGGER = Logger.getLogger( ClientListener.class.getName() );

    // TODO: NEED TO EXAMINE WHETHER THERE ARE BETTER SYNCH OPTIONS
    private Set<Message> messageFeed;

    ClientListener(Protocol protocol) {
        super(protocol);
        this.messageFeed = Collections.synchronizedSet(new HashSet<>());
    }

    Set<Message> getCurrentMessageFeed() {
        Set<Message> feedCopy = Collections.synchronizedSet(new HashSet<>());
        feedCopy.addAll(this.messageFeed);
        return feedCopy;
    }

    @Override
    public void run() {
        int messageSize = super.getProtocol().getMessageSize();

        byte[] messageBuffer = new byte[messageSize];
        DatagramPacket packetToReceive = new DatagramPacket(new byte[messageSize], messageSize);
        try {
            while(true) {
                if (shouldThreadStop()) {
                    return;
                }
                Message newMessage = getMessageFromRemote(messageBuffer, packetToReceive);
                this.messageFeed.add(newMessage);
            }
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "ClientListener failed to receive incoming message: " + e.toString());
        } finally {
            super.closeListenSocket();
        }
    }

    private Message getMessageFromRemote(byte[] messageBuffer, DatagramPacket packetToReceive) throws IOException {
        super.receivePacket(packetToReceive);

        try (DataInputStream inputStream = new DataInputStream(
                new ByteArrayInputStream(packetToReceive.getData()))) {
            inputStream.read(messageBuffer);
        }
        return new Message(super.getProtocol(), new String(messageBuffer, "UTF8"));
    }
}
