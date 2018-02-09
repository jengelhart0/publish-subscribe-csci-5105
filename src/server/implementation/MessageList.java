package server.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

class MessageList {
    private static final Logger LOGGER = Logger.getLogger( Coordinator.class.getName() );

    private List<String> messages;
    private final Object listLock = new Object();

    MessageList() {
        this.messages = new ArrayList<>();
    }

    Set<String> getMessagesStartingAt(int index) {
        int size = this.messages.size();
        if (index > size) {
            index = size;
        }
        synchronized (listLock) {
            return new HashSet<>(this.messages.subList(index, size));
        }
    }

    Integer synchronizedAdd(String message) {
        synchronized (listLock) {
            this.messages.add(message);
            return this.messages.size() - 1;
        }
    }

}
