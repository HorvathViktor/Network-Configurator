package main.java.Configs.Protocols.Utility;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

import java.util.ArrayList;

public class NetflowConfig extends Config
{
    private ArrayList<IPv4Network> destinationServers = new ArrayList<>();
    private ArrayList<Integer> destinationPorts = new ArrayList<>();
    private boolean v9 = true;
    private Interface sourceInterface;
    private NetflowInterfaces[] interfaces;

    public record NetflowInterfaces(Interface iface, boolean inbound, boolean outbound)
    {

    }
    public NetflowConfig withNetflowServer(IPv4Network netflowServer, int port)
    {
        this.destinationServers.add(netflowServer);
        this.destinationPorts.add(port);
        return this;
    }

    public NetflowConfig withVersion9(boolean v9)
    {
        this.v9 = v9;
        return this;
    }

    public NetflowConfig withInterfaces(NetflowInterfaces[] interfaces)
    {
        this.interfaces = interfaces;
        return this;
    }

    public NetflowConfig withSourceInterface(Interface sourceInterface)
    {
        this.sourceInterface = sourceInterface;
        return this;
    }

    private void setSourceInterface()
    {
        send("ip flow-export source "+sourceInterface.getType() + sourceInterface.getInterfaceID(),"config");
    }

    private void setDestinationServer()
    {
        for (int i = 0; i < destinationServers.size(); i++)
        {
            send("ip flow-export destination " + destinationServers.get(i).getIPAddress() + " " + destinationPorts.get(i), "config");
        }
    }

    private void setVersion9()
    {
        send("ip flow-export version 9", "config");
    }

    private void collectFromInterface()
    {
        for (NetflowInterfaces iface : interfaces)
        {
            enterInterface(iface.iface);
            if (iface.inbound)
            {
                send("ip flow ingress", iface.iface.getExpectedPrompt());
            }
            if (iface.outbound)
            {
                send("ip flow egress", iface.iface.getExpectedPrompt());
            }
            exit("config");
        }
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN NETFLOW CONFIG --");
        configure();
        setDestinationServer();
        if (v9)
        {
            setVersion9();
        }
        if(isNotEmptyOrNull(sourceInterface))
        {
            setSourceInterface();
        }
        collectFromInterface();
        end();
    }
}
