package server.article;

import server.api.Message;
import server.api.Protocol;

import java.util.HashMap;

public class Article implements Message {
    private String asRawMessage;
    private HashMap<String, String> query;
    private Protocol articleProtocol;


    private Article(String message) {
        // TODO: validate message/handle exceptions
        this.asRawMessage = message;
        String[] fields = {"type", "orginator", "org", "contents"};
        String delimiter = ";";
        this.articleProtocol = new Protocol(fields, delimiter);
        makeQuery();
    }

    @Override
    public void makeQuery() {

    }

    @Override
    public boolean validate(String subscription) {
        return false;
    }
}