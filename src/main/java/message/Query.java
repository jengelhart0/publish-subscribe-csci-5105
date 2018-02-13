package message;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Query {
    private String[] fields;
    private String[] values;
    private boolean isSubscription;
    private String wildcard;
    private Map<ImmutablePair<String, String>, String> query;
    private Date lastAccess;
    private final Object lastAccessLock = new Object();

    Query(String[] fields, String[] values, String wildcard, boolean isSubscription) {
        this.fields = fields;
        this.values = values;
        this.query = new ConcurrentHashMap<>();
        this.wildcard = wildcard;
        this.isSubscription = isSubscription;
        this.lastAccess = new Date();
    }

    Query generate() {
        int i;
        int numFields = fields.length;
        String field, value;

        for (i = 0; i < numFields; i++) {
            field = fields[i];
            value = values[i];
            query.put(new ImmutablePair<>(field, value), "");

            // Messages to be published need to be added to wildcard buckets
            if (!isSubscription) {
                query.put(new ImmutablePair<>(field, wildcard), "");
            }
        }
        return this;
    }

    public void refreshAccessOffsets() {
        for(ImmutablePair<String, String> fieldValuePair: query.keySet()) {
            setLastRetrievedFor(fieldValuePair, "");
        }
    }

    public Set<ImmutablePair<String, String>> getConditions() {
        return query.keySet();
    }

    public String getLastRetrievedFor(ImmutablePair<String, String> fieldValuePair) {
        return query.get(fieldValuePair);
    }

    public void setLastRetrievedFor(ImmutablePair<String, String> fieldValuePair, String lastRetrieved) {
        query.put(fieldValuePair, lastRetrieved);
    }

    public Date getLastAccess() {
        synchronized (lastAccessLock) {
            return lastAccess;
        }
    }

    public void setLastAccess(Date lastAccess) {
        synchronized (lastAccessLock) {
            this.lastAccess = lastAccess;
        }
    }
}
