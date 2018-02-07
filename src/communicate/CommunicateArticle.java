package communicate;

import shared.Protocol;

public interface CommunicateArticle extends Communicate {
    String[] FIELDS = {"type", "orginator", "org"};
    char DELIMITER = ';';
    int MESSAGESIZE = 120;
    String NAME = "CommunicateArticle";

    enum RemoteMessageCall {
        JOIN, LEAVE, PUBLISH, SUBSCRIBE, UNSUBSCRIBE
    }

    Protocol ARTICLE_PROTOCOL = new Protocol(FIELDS, DELIMITER, MESSAGESIZE);
}
