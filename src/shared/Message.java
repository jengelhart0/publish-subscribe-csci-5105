package shared;

import server.implementation.Query;
import server.implementation.TripleKeyValueStore;

public class Message {

//    private String creatorIp;
//    private String creatorPort;

    private Protocol protocol;
    private String asRawMessage;
    private Query query;
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

//        if(isSubscription) {
//            this.storeAccesses = new StoreAccesses(protocol);
//        }

//        this.creatorIp = creatorIp;
//        this.creatorPort = creatorPort;
    }
//
//    public Message(Protocol protocol, String rawMessage) {
//        this(protocol, rawMessage, null, null);
//    }

    private boolean validate(boolean isSubscription) {
        return protocol.validate(asRawMessage, isSubscription);
    }

    public String asRawMessage() {
        return asRawMessage;
    }

    void setQuery() {
        this.query = TripleKeyValueStore.getInstance().generateQuery(this, this.protocol);
    }

    public Query getQuery() {
        return this.query;
    }

    public Protocol getProtocol() {
        return protocol;
    }

//    public int getNextOffsetFor(String field) {
//        return this.storeAccesses.getNextOffsetFor(field);
//    }
//
//    public void setNextOffsetFor(String field, int offset) {
//        this.storeAccesses.setNextOffsetFor(field, offset);
//    }
//
//    public Date getLastAccess() {
//        return this.storeAccesses.getLastAccess();
//    }
//
//    public void setLastAccess(Date current) {
//        this.storeAccesses.setLastAccess(current);
//    }

    public boolean isSubscription() {
        return isSubscription;
    }

//    public void refreshStoreAccessOffsets() {
//        this.storeAccesses.refreshOffsets();
//    }

    public String getContents() {
        return "";
    }

    public boolean isHeartbeat() {
        return false;
    }
}