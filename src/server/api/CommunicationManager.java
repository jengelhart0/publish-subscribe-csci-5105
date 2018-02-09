package server.api;

import shared.Message;

public interface CommunicationManager {

    enum Call {
        PUBLISH, SUBSCRIBE, UNSUBSCRIBE, PULL_MATCHES;
    }

    Runnable task(Message message, Call call);
    void subscribe(Message message);
    void unsubscribe(Message message);
    void publish(Message message);
    void pullSubscriptionMatches();
}
