package shared;

import java.util.HashMap;

public class ArticleSubscription implements Message {

    private Protocol subscriptionProtocol;

    private String asRawMessage;
    private HashMap<String, String> query;

    private ArticleSubscription(String subscription) {
        // TODO: validate/exception handle
        this.asRawMessage = subscription;
        String[] fields = {"type", "orginator", "org"};
        String delimiter = ";";
        this.subscriptionProtocol = new Protocol(fields, delimiter);

        generateQuery();
    }

    @Override
    public boolean validate(String subscription) {
        return false;
    }

    @Override
    public void generateQuery() {

    }
}

