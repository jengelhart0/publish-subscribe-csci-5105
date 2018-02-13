package server;

import message.Message;

import org.apache.commons.lang3.tuple.ImmutablePair;

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
    public SortedSet<String> retrieve(Message subscription) {
        if (!subscription.isSubscription()) {
            throw new IllegalArgumentException("Store retrieve() received non-subscription message.");
        }

        freshenOffsetsIfNecessary(subscription);
        subscription.setLastAccess(new Date());

        SortedSet<String> matchedPublications = null;
        SortedSet<String> candidates;

        Set<ImmutablePair<String, String>> conditions = subscription.getQueryConditions();

        for(ImmutablePair<String, String> condition: conditions) {
            String lastRetrieved = subscription.getLastRetrievedFor(condition);

            candidates = (
                    store.containsKey(condition) ?
                    store.get(condition).getPublicationsStartingAt(lastRetrieved)
                    : new TreeSet<>());

            if (!candidates.isEmpty()) {
                subscription.setLastRetrievedFor(condition, candidates.last());
            }

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
            if(listToAddPublicationTo == null) {
                listToAddPublicationTo = new PublicationList();
                store.put(condition, listToAddPublicationTo);
            }
            listToAddPublicationTo.synchronizedAdd(message.asRawMessage());
            message.setLastRetrievedFor(condition, message.asRawMessage());
        }
        return true;
    }
}
