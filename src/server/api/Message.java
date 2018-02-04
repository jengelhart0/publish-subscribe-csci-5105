package server.api;

import java.util.HashMap;

public interface Message {
//    HashMap<String, String> getQuery();
//    String getRaw();
    void makeQuery();
    boolean validate(String subscription);
}
