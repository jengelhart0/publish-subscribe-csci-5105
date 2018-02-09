package Message;

import org.apache.commons.lang3.tuple.ImmutablePair;
import server.implementation.Query;
import server.implementation.TripleKeyValueStore;

import java.util.Date;
import java.util.Set;

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

    }

    private boolean validate(boolean isSubscription) {
        return protocol.validate(asRawMessage, isSubscription);
    }

    public String asRawMessage() {
        return asRawMessage;
    }

    private void setQuery() {
        this.query = TripleKeyValueStore.getInstance().generateQuery(this, this.protocol);
    }

    public Set<ImmutablePair<String,String>> getQueryConditions() {
        return this.query.getConditions();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getNextAccessOffsetFor(ImmutablePair<String, String> condition) {
        return this.query.getNextAccessOffsetFor(condition);
    }

    public void setNextAccessOffsetFor(ImmutablePair<String, String> condition, int offset) {
        this.query.setNextAccessOffsetFor(condition, offset);
    }

    public Date getLastAccess() {
        return this.query.getLastAccess();
    }

    public void setLastAccess(Date current) {
        this.query.setLastAccess(current);
    }

    public boolean isSubscription() {
        return isSubscription;
    }

    public void refreshAccessOffsets() {
        this.query.refreshAccessOffsets();
    }
}