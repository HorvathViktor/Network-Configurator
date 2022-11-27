package main.java.Configs.Protocols.Interface;


import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;

public class IPv6InterfaceConfig extends InterfaceConfig
{
    private final IPv6Network network;

    public IPv6InterfaceConfig(Interface iface, IPv6Network network)
    {
        super(iface);
        this.network = network;
    }


    private void addIpv6GlobalAddress()
    {
        send("ipv6 address " + network.getIPAddress() + network.getPrefix(), iface.getExpectedPrompt());
    }

    private void addIpv6LinkLocalAddress()
    {
        send("ipv6 address " + network.getLinkLocalAddress() + " link-local", iface.getExpectedPrompt());
    }

    private void enableIpv6()
    {
        send("ipv6 enable", iface.getExpectedPrompt());
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN INTERFACE IPv6 CONFIG --");
        configure();
        enterInterface(iface);
        if (isNotEmptyOrNull(network.getLinkLocalAddress()))
        {
            addIpv6LinkLocalAddress();
        }
        else
        {
            enableIpv6();
        }
        if (isNotEmptyOrNull(network))
        {
            addIpv6GlobalAddress();
        }
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
