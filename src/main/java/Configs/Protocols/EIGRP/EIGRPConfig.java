package main.java.Configs.Protocols.EIGRP;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class EIGRPConfig extends Config
{

    private final int autonomousSystem;
    private String routerID;
    private boolean autoSummary = false;
    private boolean redistributeStatic = false;
    private IPv4Network[] networks;
    private Interface[] interfaces;

    public EIGRPConfig(int autonomousSystem)
    {
        if (autonomousSystem < 1 || autonomousSystem > 65535)
        {
            throw new IllegalArgumentException("EIGRP Autonomous System number is incorrect");
        }
        this.autonomousSystem = autonomousSystem;
    }

    public EIGRPConfig withRouterID(String routerID)
    {
        this.routerID = routerID;
        return this;
    }

    public EIGRPConfig withAutoSummary(boolean autoSummary)
    {
        this.autoSummary = autoSummary;
        return this;
    }

    public EIGRPConfig withStaticRedistribution(boolean redistributeStatic)
    {
        this.redistributeStatic = redistributeStatic;
        return this;
    }

    public EIGRPConfig withNetworks(IPv4Network[] networks)
    {
        this.networks = networks;
        return this;
    }

    public EIGRPConfig withPassiveInterfaces(Interface[] interfaces)
    {
        this.interfaces = interfaces;
        return this;
    }

    private void advertiseNetworks()
    {
        for (IPv4Network network : networks)
        {
            send("network " + network.getIPAddress() + " " + network.getWildcard(), "config-router");
        }
    }

    private void passiveInterfaces()
    {
        for (Interface iface : interfaces)
        {
            send("passive-interface " + iface.getType().toString() + iface.getInterfaceID(), "config-router");
        }
    }

    private void redistributeStatic()
    {
        send("redistribute static", "config-router");
    }

    private void setRouterID()
    {
        send("eigrp router-id " + routerID, "config-router");
    }

    private void noAutoSummary()
    {
        send("no auto-summary", "config-router");
    }

    private void noShutdown()
    {
        send("no shutdown", "config-router");
    }

    private void enterEIGRPSubConfig()
    {

        send("router eigrp " + autonomousSystem, "config-router");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN EIGRPv4 CONFIG --");
        configure();
        enterEIGRPSubConfig();
        if (isNotEmptyOrNull(routerID))
        {
            setRouterID();
        }
        if (!autoSummary)
        {
            noAutoSummary();
        }
        if (redistributeStatic)
        {
            redistributeStatic();
        }
        noShutdown();
        if (isNotEmptyOrNull(networks))
        {
            advertiseNetworks();
        }
        if (isNotEmptyOrNull(interfaces))
        {
            passiveInterfaces();
        }
        end();
    }
}
