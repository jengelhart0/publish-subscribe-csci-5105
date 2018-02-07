package server.api;

import shared.Message;
import shared.Protocol;

public interface CommunicationManager {

    enum Call {
        PUBLISH, SUBSCRIBE, UNSUBSCRIBE, PULL_MATCHES;
    }

    Runnable task(Message message, Call call);
    boolean subscribe(Message message);
    boolean unsubscribe(Message message);
    boolean publish(Message message);
    boolean pullSubscriptionMatches();
}
