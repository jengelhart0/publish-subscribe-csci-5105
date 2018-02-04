package shared;

import java.util.HashMap;

public class Protocol {
    private String[] fields;
    private String delimiter;
    private int messageSize;

    Protocol(String[] fields, String delimiter, int messageSize) {
        this.fields = fields;
        this.delimiter = delimiter;
    }
    public HashMap<String, String> createQuery(String message) {
        return null;
    }

    public int getMessageSize() {
        return this.messageSize;
    }
}
