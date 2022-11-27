package main.java.Configs.Protocols.Interface;

import main.java.BasicNetworking.*;

public class SubinterfaceConfig extends InterfaceConfig
{
    public enum Encapsulation
    {
        dot1Q, isl
    }

    private final int VLAN;
    private final Encapsulation encapsulation;
    private IPv4Network ipv4Network;
    private IPv6Network ipv6Network;
    private Interface hostInterface;
    private boolean autoLinkLocal;

    public SubinterfaceConfig(Subinterface iface, Encapsulation encapsulation)
    {
        super(iface);
        this.VLAN = iface.getVlan();
        this.encapsulation = encapsulation;
    }

    public SubinterfaceConfig withIpv4Network(IPv4Network ipv4Network)
    {
        this.ipv4Network = ipv4Network;
        return this;
    }

    public SubinterfaceConfig withIpv6Network(IPv6Network ipv6Network)
    {
        this.ipv6Network = ipv6Network;
        return this;
    }
    public SubinterfaceConfig withAutoLinkLocal(boolean autoLinkLocal)
    {
        this.autoLinkLocal = autoLinkLocal;
        return this;
    }

    private void enterSubinterface()
    {
        send("interface " + iface.getType() + iface.getInterfaceID(), iface.getExpectedPrompt());
    }

    private void setEncapsulation()
    {
        send("encapsulation " + encapsulation + " " + VLAN, iface.getExpectedPrompt());
    }

    private void assignIpv4AddressToInterface()
    {
        send("ip address " + ipv4Network.getIPAddress() + " " + ipv4Network.getMask(), iface.getExpectedPrompt());
    }

    private void addIpv6GlobalAddress()
    {
        send("ipv6 address " + ipv6Network.getIPAddress() + ipv6Network.getPrefix(), iface.getExpectedPrompt());
    }

    private void addIpv6LinkLocalAddress()
    {
        send("ipv6 address " + ipv6Network.getLinkLocalAddress() + " link-local", iface.getExpectedPrompt());
    }

    private void enableIpv6()
    {
        send("ipv6 enable", iface.getExpectedPrompt());
    }
    private void bringUpHostInterface()
    {
        enterInterface(hostInterface);
        bringUp();
        exit("config");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN SUBINTERFACE CONFIG --");
        configure();
        if(isNotEmptyOrNull(hostInterface))
        {
            bringUpHostInterface();
        }
        enterSubinterface();
        setEncapsulation();
        if (isNotEmptyOrNull(ipv4Network))
        {
            assignIpv4AddressToInterface();
        }
        if (isNotEmptyOrNull(ipv6Network))
        {
            if (isNotEmptyOrNull(ipv6Network.getLinkLocalAddress()))
            {
                addIpv6LinkLocalAddress();
            }
            else
            {
                enableIpv6();
            }
            if (isNotEmptyOrNull(ipv6Network))
            {
                addIpv6GlobalAddress();
            }
        }
        else
        {
            if(ipv6Network != null && isNotEmptyOrNull(ipv6Network.getLinkLocalAddress()))
            {
                addIpv6LinkLocalAddress();
            }
            if(autoLinkLocal)
            {
                enableIpv6();
            }
        }
        end();
    }
}
