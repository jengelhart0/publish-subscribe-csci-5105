package shared;

import java.util.HashMap;

public class Protocol {
    private String[] fields;
    private String delimiter;
    private int messageSize;

    Protocol(String[] fields, String delimiter, int messageSize) {
        this.fields = fields;
        this.delimiter = delimiter;
        this.messageSize = messageSize;
    }
    public HashMap<String, String> createQuery(String message) {
        return null;
    }

    public int getMessageSize() {
        return this.messageSize;
    }

    public boolean validate(String message) {
        return false;
    }

}
