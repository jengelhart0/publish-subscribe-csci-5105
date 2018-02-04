package server.article;

import server.api.Protocol;
import server.api.Message;

import java.util.HashMap;

public class ArticleSubscription implements Message {
    private String asRawMessage;
    private HashMap<String, String> query;
    private Protocol subscriptionProtocol;

    private ArticleSubscription(String subscription) {
        // TODO: validate/exception handle
        this.asRawMessage = subscription;
        String[] fields = {"type", "orginator", "org"};
        String delimiter = ";";
        this.subscriptionProtocol = new Protocol(fields, delimiter);

        makeQuery();
    }

    @Override
    public boolean validate(String subscription) {
        return false;
    }

    @Override
    public void makeQuery() {

    }
}

