package shared;

import java.util.Map;

public class Message {

//    private String creatorIp;
//    private String creatorPort;

    private Protocol protocol;
    private String asRawMessage;
    private Map<String, String> query;
    private boolean isSubscription;


    public Message(Protocol protocol, String rawMessage, boolean isSubscription) {
        // TODO: validate message/handle exceptions

        this.protocol = protocol;

        if (!validate(isSubscription)) {
            throw new IllegalArgumentException();
        }

        this.asRawMessage = rawMessage;
        this.isSubscription = isSubscription;
        setQuery();

//        this.creatorIp = creatorIp;
//        this.creatorPort = creatorPort;
    }
//
//    public Message(Protocol protocol, String rawMessage) {
//        this(protocol, rawMessage, null, null);
//    }

    public boolean validate(boolean isSubscription) {
        return protocol.validate(asRawMessage, isSubscription);
    }

    public String asRawMessage() {
        return asRawMessage;
    }

    public void setQuery() {
        this.query = protocol.generateQuery(asRawMessage);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getContents() {
        return "";
    }

    public boolean isHeartbeat() {
        return false;
    }
}