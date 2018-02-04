package shared;

import java.util.HashMap;

public class Protocol {
    String[] fields;
    String delimiter;

    public Protocol(String[] fields, String delimiter) {
        this.fields = fields;
        this.delimiter = delimiter;
    }
    public HashMap<String, String> createQuery(String message) {

    }
}
