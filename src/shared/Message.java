package shared;

import java.util.Date;
import java.util.Map;

public class Message {

//    private String creatorIp;
//    private String creatorPort;

    private Protocol protocol;
    private String asRawMessage;
    private Map<String, String> query;
    private boolean isSubscription;
    private StoreAccesses storeAccesses = null;


    public Message(Protocol protocol, String rawMessage, boolean isSubscription) {
        // TODO: validate message/handle exceptions

        this.protocol = protocol;

        if (!validate(isSubscription)) {
            throw new IllegalArgumentException();
        }

        this.asRawMessage = rawMessage;
        this.isSubscription = isSubscription;
        setQuery();

        if(isSubscription) {
            this.storeAccesses = new StoreAccesses(protocol);
        }

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

    public Map<String, String> getQuery() {
        return this.query;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getLastOffsetFor(String field) {
        return this.storeAccesses.getLastOffsetFor(field);
    }

    public void setLastOffsetFor(String field, int offset) {
        this.storeAccesses.setLastOffsetFor(field, offset);
    }

    public Date getLastAccess() {
        return this.storeAccesses.getLastAccess();
    }

    public void setLastAccess(Date current) {
        this.storeAccesses.setLastAccess(current);
    }

    public boolean isSubscription() {
        return isSubscription;
    }

    public void refreshOffsets() {
        for (String field : this.protocol.getSubscriptionFields()) {
            this.storeAccesses.setLastOffsetFor(field, 0);
        }
    }

    public String getContents() {
        return "";
    }

    public boolean isHeartbeat() {
        return false;
    }
}