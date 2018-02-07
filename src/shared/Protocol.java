package shared;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Protocol {
    // It is understood that all messages have data/payload fields,
    // so we only indicate others (i.e., the subscription fields).
    private String[] subscriptionFields;
    private String delimiter;
    private int messageSize;

    public Protocol(String[] fields, String delimiter, int messageSize) {
        this.subscriptionFields = fields;
        this.delimiter = delimiter;
        this.messageSize = messageSize;
    }

    public Map<String, String> generateQuery(String message) {
        //TODO: need to validate first, or okay to let caller worry?
        String[] parsedValues = parse(message);
        String[] messageFields = this.subscriptionFields;

        Map<String, String> query = new HashMap<>();

        int i;
        int numFields = messageFields.length;

        for (i = 0; i < numFields; i++) {
            query.put(messageFields[i], parsedValues[i]);
        }
        return query;
    }

    private String[] parse(String message) {
        return message.split(this.delimiter);
    }

    public int getMessageSize() {
        return this.messageSize;
    }

    public boolean validate(String message, boolean isSubscription) {
        if (!isBasicallyValid(message)) {
            return false;
        }
        String[] parsedMessage = parse(message);
        boolean lastFieldEmpty = Pattern.matches(
                "^\\s+$", parsedMessage[messageSize - 1]);

        if(isSubscription) {
            return lastFieldEmpty;
        }
        return !lastFieldEmpty;
    }

    private boolean isBasicallyValid(String message) throws IllegalArgumentException {
        if(message.length() != this.messageSize) {
            return false;
        }

        String[] parsedValues = parse(message);
        int valuesLength = parsedValues.length;
        if(valuesLength != subscriptionFields.length + 1) {
            return false;
        }

        boolean nonEmptyValueExists = false;
        int i = 0;
        while(!nonEmptyValueExists && i < valuesLength) {
            if (!(parsedValues[i].equals(""))) {
                nonEmptyValueExists = true;
            }
            i++;
        }
        return nonEmptyValueExists;
    }
}
