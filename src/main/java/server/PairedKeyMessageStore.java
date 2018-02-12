package server;

import message.Message;

import org.apache.commons.lang3.tuple.ImmutablePair;
import message.Protocol;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PairedKeyMessageStore implements MessageStore {
    private static PairedKeyMessageStore ourInstance = new PairedKeyMessageStore();

    private Map<ImmutablePair<String, String>, PublicationList> store;
    private Date lastStoreFlush;

    public static PairedKeyMessageStore getInstance() {
        return ourInstance;
    }

    private PairedKeyMessageStore() {
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

        Set<String> matchedPublications = null;
        Set<String> candidates;

        Set<ImmutablePair<String, String>> conditions = subscription.getQueryConditions();

        for(ImmutablePair<String, String> condition: conditions) {
            int nextOffset = subscription.getNextAccessOffsetFor(condition);
            candidates = store.get(condition)
                    .getPublicationsStartingAt(nextOffset);

            subscription.setNextAccessOffsetFor(condition, nextOffset + candidates.size());

            if (matchedPublications != null) {
                matchedPublications.retainAll(candidates);
            } else {
                matchedPublications = candidates;
            }
        }
        return matchedPublications;
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
            PublicationList listToAddPublicationTo = store.get(condition);
            Integer messageIdx = listToAddPublicationTo.synchronizedAdd(message.asRawMessage());
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
