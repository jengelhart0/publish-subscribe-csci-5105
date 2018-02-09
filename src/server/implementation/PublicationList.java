package server.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

class PublicationList {
    private static final Logger LOGGER = Logger.getLogger( Coordinator.class.getName() );

    private List<String> publications;
    private final Object listLock = new Object();

    PublicationList() {
        this.publications = new ArrayList<>();
    }

    Set<String> getPublicationsStartingAt(int index) {
        int size = this.publications.size();
        if (index > size) {
            index = size;
        }
        synchronized (listLock) {
            return new HashSet<>(this.publications.subList(index, size));
        }
    }

    Integer synchronizedAdd(String message) {
        synchronized (listLock) {
            this.publications.add(message);
            return this.publications.size() - 1;
        }
    }

}
