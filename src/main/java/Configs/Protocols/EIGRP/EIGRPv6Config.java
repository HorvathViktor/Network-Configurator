package main.java.Configs.Protocols.EIGRP;


import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class EIGRPv6Config extends Config
{
    private final int autonomousSystem;
    private final String routerID;
    private boolean redistributeStatic = false;
    private Interface[] interfaces;
    private Interface[] interfacesToAdvertise;

    public EIGRPv6Config(int autonomousSystem, String routerID)
    {
        this.autonomousSystem = autonomousSystem;
        this.routerID = routerID;
    }


    public EIGRPv6Config withStaticRedistribution(boolean redistribution)
    {
        this.redistributeStatic = redistribution;
        return this;
    }

    public EIGRPv6Config withPassiveInterfaces(Interface[] interfaces)
    {
        this.interfaces = interfaces;
        return this;
    }
    public EIGRPv6Config withInterfacesToAdvertise(Interface[] interfacesToAdvertise)
    {
        this.interfacesToAdvertise = interfacesToAdvertise;
        return this;
    }

    private void advertiseInterfaces()
    {
        for (Interface iface : interfacesToAdvertise)
        {
            enterInterface(iface);
            send("ipv6 eigrp " + autonomousSystem, iface.getExpectedPrompt());
            exit("config");
        }
    }

    private void passiveInterfaces()
    {
        for (Interface iface : interfaces)
        {
            send("passive-interface " + iface.getType().toString() + iface.getInterfaceID(), "config-rtr");
        }
    }

    private void redistributeStatic()
    {
        send("redistribute static", "config-rtr");
    }

    private void setRouterID()
    {
        send("eigrp router-id " + routerID, "config-rtr");
    }

    private void noShutdown()
    {
        send("no shutdown", "config-rtr");
    }

    private void enterEIGRPSubConfig()
    {
        if (autonomousSystem < 1 || autonomousSystem > 65535)
        {
            throw new IllegalArgumentException("EIGRPv6 Autonomous System number is incorrect");
        }
        send("ipv6 router eigrp " + autonomousSystem, "config-rtr");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN EIGRPv6 CONFIG --");
        configure();
        enterEIGRPSubConfig();
        setRouterID();
        if (redistributeStatic)
        {
            redistributeStatic();
        }
        if (isNotEmptyOrNull(interfaces))
        {
            passiveInterfaces();
        }
        noShutdown();
        exit("config");
        advertiseInterfaces();
        end();
    }
}
