package message;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Date;
import java.util.Set;

public class Message {

    private Protocol protocol;
    private String asRawMessage;
    private Query query;
    private boolean isSubscription;

    public Message(Protocol protocol, String rawMessage, boolean isSubscription) {
        // TODO: validate message/handle exceptions

        this.protocol = protocol;

        int messageSize = protocol.getMessageSize();
        if(rawMessage.length() < messageSize) {
            this.asRawMessage = protocol.padMessage(rawMessage);
        }

        if (!validate(isSubscription)) {
            throw new IllegalArgumentException("Was an invalid subscription: " +
                    this.asRawMessage + "length: " + this.asRawMessage.length());
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
        this.query = generateQuery(this, this.protocol);
    }

    public Query generateQuery(Message message, Protocol protocol) {
        return new Query(protocol.getQueryFields(),
                protocol.parse(message.asRawMessage()),
                protocol.getWildcard(),
                message.isSubscription())

                .generate();
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