package main.java.RemoteConnection;

import java.io.IOException;

public interface Connection
{
    void connect(String destinationIP, int destinationPort) throws IOException;
    void send(String command);
    void disconnect();
    String popPrompt();
}
