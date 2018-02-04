package shared;

import java.util.HashMap;

public interface Message {
//    HashMap<String, String> getQuery();
//    String getRaw();
    void generateQuery();
    boolean validate(String subscription);
}
