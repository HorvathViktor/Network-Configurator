package main.java.Configs.Protocols.DHCP;

import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

import java.util.ArrayList;

public class DHCPRelayConfig extends Config
{

    private final Interface iface;
    private ArrayList<IPv4Network> networkV4 = new ArrayList<>();
    private ArrayList<IPv6Network> networkV6 = new ArrayList<>();


    public DHCPRelayConfig(Interface iface)
    {
        this.iface = iface;
    }

    public DHCPRelayConfig withIPv4Network(IPv4Network network)
    {
        networkV4.add(network);
        return this;
    }
    public DHCPRelayConfig withIPv6Network(IPv6Network network)
    {
        networkV6.add(network);
        return this;
    }
    private void helperAddress()
    {
        for (IPv4Network network : networkV4)
        {
            send("ip helper-address "+network.getIPAddress(), iface.getExpectedPrompt());
        }
    }
    private void dhcpRelay()
    {
        for (IPv6Network network : networkV6)
        {
            send("ipv6 dhcp relay destination  "+network.getIPAddress(), iface.getExpectedPrompt());
        }
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN DHCP RELAY CONFIG --");
        configure();
        enterInterface(iface);
        if(isNotEmptyOrNull(networkV4))
        {
            helperAddress();
        }
        if(isNotEmptyOrNull(networkV6))
        {
            dhcpRelay();
        }
        end();
    }
}
