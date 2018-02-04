package server.api;

public interface CommunicationManager {
    boolean Subscribe(Message message);
    boolean Unsubscribe(Message message);
    boolean Publish(Protocol messageProtocol);
    boolean Ping();
}
