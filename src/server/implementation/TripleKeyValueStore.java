package server.implementation;

import server.api.MessageStore;
import shared.Message;

import org.apache.commons.lang3.tuple.ImmutablePair;

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

        Map<String, String> query = subscription.getQuery();
        freshenOffsetsIfNecessary(subscription);

        Set<String> matchedMessages = null;
        Set<String> candidates;
        for(String field: query.keySet()) {
            int lastOffset = subscription.getLastOffsetFor(field);
            candidates = store.get( new ImmutablePair<>(field, query.get(field)) )
                    .getMessagesStartingAt(lastOffset);

            if (matchedMessages == null) {
                matchedMessages = candidates;
            } else {
                matchedMessages.retainAll(candidates);
            }
        }
        return matchedMessages;
    }

    private void freshenOffsetsIfNecessary(Message subscription) {
        if (subscription.getLastAccess().compareTo(lastStoreFlush) < 0) {
            subscription.refreshOffsets();
        }
    }

    @Override
    public boolean publish(Message message) {

        return false;
    }
}
