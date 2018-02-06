package communicate;

import shared.Protocol;

public interface CommunicateArticle extends Communicate {
    String[] FIELDS = {"type", "orginator", "org"};
    char DELIMITER = ';';
    int MESSAGESIZE = 120;
    int PINGINTERVAL = 20;
    String NAME = "CommunicateArticle";


    Protocol ARTICLE_PROTOCOL = new Protocol(FIELDS, DELIMITER, MESSAGESIZE);
}
