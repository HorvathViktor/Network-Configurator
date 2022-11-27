package main.java.Configs.Protocols.Redundancy;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.Interface;

public class HSRPv4Config extends HSRPConfig
{
    private final IPv4Network virtualIPv4;

    public HSRPv4Config(Interface iface, int group, IPv4Network virtualIPv4)
    {
        super(iface, group);
        this.virtualIPv4 = virtualIPv4;
    }

    private void addHSRPv4VirtualAddress()
    {
        send("standby " + group + " ip " + virtualIPv4.getIPAddress(), iface.getExpectedPrompt());
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN HSRPv4 CONFIG --");
        configure();
        enterInterface(iface);
        addHSRPv4VirtualAddress();
        if(priority != -1)
        {
            setPriority();
        }
        if (overthrow)
        {
            preempt();
        }
        if(version2)
        {
            configureHSRPVersion2();
        }
        end();
    }
}
