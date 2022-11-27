package main.java.Configs.Protocols.Redundancy;


import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;

public class HSRPv6Config extends HSRPConfig
{
    private IPv6Network virtualIPv6;

    public HSRPv6Config(Interface iface, int group, IPv6Network virtualIPv6)
    {
        super(iface, group);
        this.virtualIPv6 = virtualIPv6;
    }

    private void addHSRPv6VirtualAddress()
    {
        send("standby " + group + " ipv6 " + virtualIPv6.getLinkLocalAddress(), iface.getExpectedPrompt());
    }

    private void addHSRPv6AutoAddress()
    {
        send("standby " + group + " ipv6 autoconfig", iface.getExpectedPrompt());
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN HSRPv6 CONFIG --");
        configure();
        enterInterface(iface);
        if (virtualIPv6 == null || virtualIPv6.getLinkLocalAddress() == null)
        {
            addHSRPv6AutoAddress();
        }
        else
        {
            addHSRPv6VirtualAddress();
        }

        if (priority != -1)
        {
            setPriority();
        }
        if (overthrow)
        {
            preempt();
        }
        if (version2)
        {
            configureHSRPVersion2();
        }
        end();
    }
}
