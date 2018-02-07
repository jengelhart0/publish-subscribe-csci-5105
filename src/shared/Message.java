package shared;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private int MESSAGESIZE = 120;

    private String creatorIp;
    private String creatorPort;

    private Protocol protocol;
    private String asRawMessage;
    private Map<String, String> query;


    public Message(Protocol protocol, String rawMessage, String creatorIp, String creatorPort) {
        // TODO: validate message/handle exceptions

        this.protocol = protocol;

        if (!validate()) {
            throw new IllegalArgumentException();
        }

        this.asRawMessage = rawMessage;
        this.creatorIp = creatorIp;
        this.creatorPort = creatorPort;

        generateQuery();
    }

    public Message(Protocol protocol, String rawMessage) {
        this(protocol, rawMessage, null, null);
    }

    public boolean validate() {
        return this.protocol.validate(this.asRawMessage);
    }

    public String asRawMessage() {
        return this.asRawMessage;
    }

    public void generateQuery() {

    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public String getContents() {
        return "";
    }

    public boolean isHeartbeat() {
        return false;
    }

}