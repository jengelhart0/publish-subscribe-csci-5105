package communicate;

import shared.Protocol;

import java.net.InetAddress;

public interface CommunicateArticle extends Communicate {
    String NAME = "CommunicateArticle";

    int MAXCLIENTS = 1000;

    int SERVER_LIST_SIZE = 1024;

    String REGISTRY_SERVER_IP = "dio.cs.umn.edu";
    int REGISTRY_SERVER_PORT = 5105;
    int HEARTBEAT_PORT = 8888;

    Protocol ARTICLE_PROTOCOL = new Protocol(
            new String[]{"type", "orginator", "org"},
            ";",
            "",
            120);
}
