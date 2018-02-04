package shared;

public interface Message {
//    HashMap<String, String> getQuery();
    String asRawMessage();
    void generateQuery();
    boolean validate();
    Protocol getProtocol();
}
