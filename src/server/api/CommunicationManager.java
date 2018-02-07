package server.api;

import shared.Message;
import shared.Protocol;

public interface CommunicationManager {

    enum Call {
        PUBLISH, SUBSCRIBE, UNSUBSCRIBE, PULL_MATCHES;
    }

    boolean subscribe(Message message);
    boolean unsubscribe(Message message);
    boolean publish(Message message);
    boolean pullSubscriptionMatches();
}
