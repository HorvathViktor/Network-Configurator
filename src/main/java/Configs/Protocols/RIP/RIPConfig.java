package main.java.Configs.Protocols.RIP;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class RIPConfig extends Config
{
    private boolean redistributeStatic = false;
    private boolean defaultInformationOriginate = false;
    private boolean autoSummary = false;
    private boolean version2 = true;
    private Interface[] interfaces;
    private IPv4Network[] networks;


    public RIPConfig withAutoSummary(boolean autoSummary)
    {
        this.autoSummary = autoSummary;
        return this;
    }

    public RIPConfig withVersion2(boolean version2)
    {
        this.version2 = version2;
        return this;
    }

    public RIPConfig withDefaultRouteAdvertisement(boolean defaultRouteAdvertisement)
    {
        this.defaultInformationOriginate = defaultRouteAdvertisement;
        return this;
    }

    public RIPConfig withStaticRedistribution(boolean redistributeStatic)
    {
        this.redistributeStatic = redistributeStatic;
        return this;
    }

    public RIPConfig withNetworks(IPv4Network[] networks)
    {
        this.networks = networks;
        return this;
    }

    public RIPConfig withPassiveInterfaces(Interface[] interfaces)
    {
        this.interfaces = interfaces;
        return this;
    }

    private void advertiseNetworks()
    {
        for (IPv4Network network : networks)
        {
            send("network " + network.getIPAddress(), "config-router");
        }
    }

    private void passiveInterfaces()
    {
        for (Interface iface : interfaces)
        {
            send("passive-interface " + iface.getType() + iface.getInterfaceID(), "config-router");
        }
    }

    private void redistributeStatic()
    {
        send("redistribute static", "config-router");
    }

    private void defaultInformationOriginate()
    {
        send("default-information originate", "config-router");
    }

    private void noShutdown()
    {
        send("no shutdown", "config-router");
    }

    private void noAutoSummary()
    {
        send("no auto-summary", "config-router");
    }

    private void setVersion2()
    {
        send("version 2", "config-router");
    }

    private void enterRIPSubConfig()
    {
        send("router rip", "config-router");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN RIP CONFIG --");
        configure();
        enterRIPSubConfig();
        if (version2)
        {
            setVersion2();
        }
        if (!autoSummary)
        {
            noAutoSummary();
        }
        if (redistributeStatic)
        {
            redistributeStatic();
        }
        if (defaultInformationOriginate)
        {
            defaultInformationOriginate();
        }
        if (isNotEmptyOrNull(networks ))
        {
            advertiseNetworks();
        }
        if (isNotEmptyOrNull(interfaces))
        {
            passiveInterfaces();
        }
        noShutdown();
        end();
    }
}
