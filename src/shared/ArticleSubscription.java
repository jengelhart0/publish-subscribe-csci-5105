package shared;

import java.util.HashMap;

public class ArticleSubscription implements Message {
    private int MESSAGESIZE = 120;
    private Protocol subscriptionProtocol;

    private String asRawMessage;
    private HashMap<String, String> query;

    private ArticleSubscription(String subscription) {
        // TODO: validate/exception handle
        this.asRawMessage = subscription;
        String[] fields = {"type", "orginator", "org"};
        String delimiter = ";";
        this.subscriptionProtocol = new Protocol(fields, delimiter, MESSAGESIZE);

        generateQuery();
    }

    @Override
    public String asRawMessage() {
        return this.asRawMessage;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public void generateQuery() {

    }

    @Override
    public Protocol getProtocol() {
        return this.subscriptionProtocol;
    }
}

