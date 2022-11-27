package main.java.BasicNetworking;

import java.util.ArrayList;

public final class Router
{
    private final Interface[] interfaces;
    private final String hostname;
    private final String destinationIP;
    private final int destinationPort;
    private ArrayList<InteraceMap> interaceMaps;

    public Router(Interface[] interfaces, String hostname, String destinationIP, int destinationPort)
    {
        this.interfaces = interfaces;
        this.hostname = hostname;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        interaceMaps = new ArrayList<>();
    }

    public Interface[] interfaces()
    {
        return interfaces;
    }

    public String hostname()
    {
        return hostname;
    }

    public String destinationIP()
    {
        return destinationIP;
    }

    public int destinationPort()
    {
        return destinationPort;
    }

    public ArrayList<InteraceMap> getInteraceMaps()
    {
        return interaceMaps;
    }

    public void addInterfaceMaps(InteraceMap interaceMap)
    {
        this.interaceMaps.add(interaceMap);
    }

    public record InteraceMap(Interface iface, IPv4Network ipv4Data)
        {
        }
}
