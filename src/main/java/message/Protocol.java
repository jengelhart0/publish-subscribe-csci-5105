package message;

import java.util.regex.Pattern;

public class Protocol {
    // It is understood that all messages have data/payload fields,
    // so we only indicate others (i.e., the query fields).
    private String[] queryFields;
    private String delimiter;
    private String wildcard;
    private int messageSize;

    public Protocol(String[] fields, String delimiter, String wildcard, int messageSize) {
        this.queryFields = fields;
        this.delimiter = delimiter;
        this.messageSize = messageSize;
        this.wildcard = wildcard;
    }

    public String[] parse(String message) {
        return message.split(this.delimiter);
    }

    public int getMessageSize() {
        return this.messageSize;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public String getWildcard() {
        return wildcard;
    }

    public String[] getQueryFields() {
        return queryFields;
    }

    public boolean validate(String message, boolean isSubscription) {
        if (!isBasicallyValid(message)) {
            return false;
        }
        String[] parsedMessage = parse(message);
        boolean lastFieldEmpty = Pattern.matches(
                "^" + this.wildcard + "\\s*$", parsedMessage[messageSize - 1]);

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
        if(valuesLength != queryFields.length + 1) {
            return false;
        }

        boolean nonWildcardValueExists = false;
        int i = 0;
        while(!nonWildcardValueExists && i < valuesLength) {
            if (!(parsedValues[i].equals(this.wildcard))) {
                nonWildcardValueExists = true;
            }
            i++;
        }
        return nonWildcardValueExists;
    }
}