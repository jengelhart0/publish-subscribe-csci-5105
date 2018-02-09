package server.implementation;

import server.api.MessageStore;
import shared.Message;

import org.apache.commons.lang3.tuple.ImmutablePair;
import shared.Protocol;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TripleKeyValueStore implements MessageStore {
    private static TripleKeyValueStore ourInstance = new TripleKeyValueStore();

    private Map<ImmutablePair<String, String>, MessageList> store;
    private Date lastStoreFlush;

    public static TripleKeyValueStore getInstance() {
        return ourInstance;
    }

    private TripleKeyValueStore() {
        this.store = new ConcurrentHashMap<>();
        this.lastStoreFlush = new Date();
    }

    @Override
    public Set<String> retrieve(Message subscription) {
        if (!subscription.isSubscription()) {
            throw new IllegalArgumentException("Store retrieve() received non-subscription message.");
        }

        freshenOffsetsIfNecessary(subscription);
        subscription.setLastAccess(new Date());

        Set<String> matchedMessages = null;
        Set<String> candidates;

        Set<ImmutablePair<String, String>> conditions = subscription.getQueryConditions();

        for(ImmutablePair<String, String> condition: conditions) {
            int nextOffset = subscription.getNextAccessOffsetFor(condition);
            candidates = store.get(condition)
                    .getMessagesStartingAt(nextOffset);

            subscription.setNextAccessOffsetFor(condition, nextOffset + candidates.size());

            if (matchedMessages != null) {
                matchedMessages.retainAll(candidates);
            } else {
                matchedMessages = candidates;
            }
        }
        return matchedMessages;
    }

    private void freshenOffsetsIfNecessary(Message subscription) {
        if (subscription.getLastAccess().compareTo(lastStoreFlush) < 0) {
            subscription.refreshAccessOffsets();
        }
    }

    @Override
    public boolean publish(Message message) {
        message.setLastAccess(new Date());
        Set<ImmutablePair<String, String>> conditions = message.getQueryConditions();

        for (ImmutablePair<String, String> condition : conditions) {
            MessageList listToAddMessageTo = store.get(condition);
            Integer messageIdx = listToAddMessageTo.synchronizedAdd(message.asRawMessage());
            message.setNextAccessOffsetFor(condition, messageIdx);
        }
        return true;
    }

    @Override
    public Query generateQuery(Message message, Protocol protocol) {
        return new Query(protocol.getQueryFields(),
                protocol.parse(message.asRawMessage()),
                protocol.getWildcard(),
                message.isSubscription())

                .generate();
    }
}
