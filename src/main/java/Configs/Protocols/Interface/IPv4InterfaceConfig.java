package main.java.Configs.Protocols.Interface;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.Interface;

public class IPv4InterfaceConfig extends InterfaceConfig
{

    private final IPv4Network network;

    public IPv4InterfaceConfig(Interface iface, IPv4Network network)
    {
        super(iface);
        this.network = network;
    }

    private void assignIpv4AddressToInterface()
    {
        send("ip address " + network.getIPAddress() + " " + network.getMask(), iface.getExpectedPrompt());
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN INTERFACE IPv4 CONFIG --");
        configure();
        enterInterface(iface);
        assignIpv4AddressToInterface();
        if(adminStatus)
        {
            bringUp();
        }
        else
        {
            shutdown();
        }
        end();
    }
}
