package server.implementation;

import listener.Listener;
import shared.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartbeatListener extends Listener {
    private static final Logger LOGGER = Logger.getLogger( HeartbeatListener.class.getName() );

    HeartbeatListener(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void run() {
        int messageSize = super.getProtocol().getMessageSize();
        DatagramPacket heartbeatPacket = new DatagramPacket(new byte[messageSize], messageSize);

        try {
            while (true) {
                super.receivePacket(heartbeatPacket);
                super.sendPacket(heartbeatPacket);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        } finally {
            super.closeListenSocket();
        }
    }
}
