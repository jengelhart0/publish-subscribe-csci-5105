package server.api;

import java.util.HashMap;

public class Protocol {
    String[] fields;
    String delimiter;

    public Protocol(String[] fields, String delimiter) {
        this.fields = fields;
        this.delimiter = delimiter;
    }
    public String createProtocolString(HashMap<String, String> messageQuery) {

    }

    public HashMap<String, String> createQuery(String message) {

    }
}
