package main.java.Configs.Protocols.RIP;


import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class RIPngConfig extends Config
{
    private boolean redistributeStatic = false;
    private final String ripID;
    private Interface[] interfacesToAdvertise;

    public RIPngConfig(String ripID)
    {
        this.ripID = ripID;
    }

    public RIPngConfig withStaticRedistribution(boolean redistribution)
    {
        this.redistributeStatic = redistribution;
        return this;
    }

    public RIPngConfig withInterfacesToAdvertise(Interface[] interfacesToAdvertise)
    {
        this.interfacesToAdvertise = interfacesToAdvertise;
        return this;
    }

    private void advertiseInterfaces()
    {
        for (Interface iface : interfacesToAdvertise)
        {
            enterInterface(iface);
            send("ipv6 rip " + ripID + " enable", iface.getExpectedPrompt());
            exit("config");
        }
    }

    private void redistributeStatic()
    {
        send("redistribute static", "config-rtr");
    }

    private void enterRIPSubConfig()
    {
        send("ipv6 router rip " + ripID, "config-rtr");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN RIPng CONFIG --");
        configure();
        enterRIPSubConfig();
        if (redistributeStatic)
        {
            redistributeStatic();
        }
        exit("config");
        advertiseInterfaces();
        end();
    }
}
