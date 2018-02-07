package shared;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
    // It is understood that all messages have data/payload fields,
    // so we only indicate others (i.e., the subscription fields).
    private String[] subscription_fields;
    private char delimiter;
    private int messageSize;

    public Protocol(String[] fields, char delimiter, int messageSize) {
        this.subscription_fields = fields;
        this.delimiter = delimiter;
        this.messageSize = messageSize;
    }

    public Map<String, String> createQuery(String message) {
        return null;
    }

    public String asRawMessage(Map<String, String> query) {
        return null;
    }

    public int getMessageSize() {
        return this.messageSize;
    }

    public boolean validate(String message) throws IllegalArgumentException {
        return false;
    }
}
