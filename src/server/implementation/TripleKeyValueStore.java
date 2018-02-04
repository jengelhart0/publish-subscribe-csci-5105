package server.implementation;

import shared.Protocol;
import server.api.MessageStore;
import shared.Message;

import java.util.HashSet;

public class TripleKeyValueStore implements MessageStore {
    private static TripleKeyValueStore ourInstance = new TripleKeyValueStore();

    public static TripleKeyValueStore getInstance() {
        return ourInstance;
    }

    private TripleKeyValueStore() {
    }

    @Override
    public HashSet<Protocol> match(Message message) {
        return null;
    }

    @Override
    public boolean store(Protocol messageProtocol) {
        return false;
    }
}
