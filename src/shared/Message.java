package shared;

import java.util.HashMap;

public class Message {
    private int MESSAGESIZE = 120;

    private String creatorIp;
    private String creatorPort;

    private Protocol protocol;
    private String asRawMessage;
    private HashMap<String, String> query;


    public Message(Protocol protocol, String rawMessage, String creatorIp, String creatorPort) {
        // TODO: validate message/handle exceptions

        this.protocol = protocol;
        protocol.validate(rawMessage);

        this.asRawMessage = rawMessage;
        this.creatorIp = creatorIp;
        this.creatorPort = creatorPort;

        generateQuery();
    }

    public Message(Protocol protocol, String rawMessage) {
        this(protocol, rawMessage, null, null);
    }

    public String asRawMessage() {
        return this.asRawMessage;
    }

    public void generateQuery() {

    }

    public Protocol getProtocol() {
        return this.protocol;
    }
}