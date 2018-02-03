package server.api;

public interface CommunicationManager {
    boolean Subscribe(Subscription subscription);
    boolean Unsubscribe(Subscription subscription);
    boolean Publish(Message message);
    boolean Ping();
}
