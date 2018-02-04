package shared;

import java.util.HashMap;

public class Article implements Message {
    private String creatorIp;
    private String creatorPort;

    private Protocol articleProtocol;
    private String asRawMessage;
    private HashMap<String, String> query;


    private Article(String message, String creatorIp, String creatorPort) {
        // TODO: validate message/handle exceptions

        this.asRawMessage = message;
        this.creatorIp = creatorIp;
        this.creatorPort = creatorPort;

        String[] fields = {"type", "orginator", "org", "contents"};
        String delimiter = ";";
        this.articleProtocol = new Protocol(fields, delimiter);

        generateQuery();
    }

    @Override
    public void generateQuery() {

    }

    @Override
    public boolean validate(String subscription) {
        return false;
    }
}