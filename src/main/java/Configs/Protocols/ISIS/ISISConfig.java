package main.java.Configs.Protocols.ISIS;

import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class ISISConfig extends Config
{
    private final String netID;
    private final String ISISID;
    private boolean redistributeStatic = false;
    private boolean defaultInformationOriginate = false;
    private boolean logChanges = true;
    private Interface[] passiveInterfaces;
    private Interface[] interfacesToAdvertiseIpv4;
    private Interface[] interfacesToAdvertiseIpv6;

    public ISISConfig(String netID, String ISISID)
    {
        this.netID = netID;
        this.ISISID = ISISID;
    }

    public ISISConfig withStaticRedistribution(boolean redistributeStatic)
    {
        this.redistributeStatic = redistributeStatic;
        return this;
    }

    public ISISConfig withInterfacesToAdvertiseIpv4Routes(Interface[] ipv4InterfacesToAdvertise)
    {
        this.interfacesToAdvertiseIpv4 = ipv4InterfacesToAdvertise;
        return this;
    }

    public ISISConfig withInterfacesToAdvertiseIpv6Routes(Interface[] ipv6InterfacesToAdvertise)
    {
        this.interfacesToAdvertiseIpv6 = ipv6InterfacesToAdvertise;
        return this;
    }

    public ISISConfig withPassiveInterfaces(Interface[] interfaces)
    {
        this.passiveInterfaces = interfaces;
        return this;
    }

    public ISISConfig withAdjacencyLogging(boolean logging)
    {
        this.logChanges = logging;
        return this;
    }

    public ISISConfig withDefaultRouteAdvertisement(boolean defaultRouteAdvertisement)
    {
        this.defaultInformationOriginate = defaultRouteAdvertisement;
        return this;
    }

    private void setISISNetID()
    {
        send("net " + netID, "config-router");
    }

    private void redistributeStatic()
    {
        send("redistribute static", "config-router");
    }

    private void defaultInformationOriginate()
    {
        send("default-information originate", "config-router");
    }

    private void logAdjacencyChanges()
    {
        send("log-adjacency-changes all", "config-router");
    }

    private void passiveInterfaces()
    {
        for (Interface iface : passiveInterfaces)
        {
            send("passive-interface " + iface.getType().toString() + iface.getInterfaceID(), "config-router");
        }
    }

    private void advertiseInterfacesIpv4()
    {
        for (Interface networks : interfacesToAdvertiseIpv4)
        {
            enterInterface(networks);
            send("ip router isis " + ISISID, networks.getExpectedPrompt());
            exit("config");
        }
    }

    private void advertiseInterfacesIpv6()
    {
        for (Interface networks : interfacesToAdvertiseIpv6)
        {
            enterInterface(networks);
            send("ipv6 router isis " + ISISID, networks.getExpectedPrompt());
            exit("config");
        }
    }

    private void enableISIS()
    {
        send("no protocol shutdown", "config-router");
    }

    private void enterISISSubConfig()
    {
        send("router isis " + ISISID, "config-router");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN IS-IS CONFIG --");
        configure();
        enterISISSubConfig();
        setISISNetID();
        if (redistributeStatic)
        {
            redistributeStatic();
        }
        if (defaultInformationOriginate)
        {
            defaultInformationOriginate();
        }
        if (logChanges)
        {
            logAdjacencyChanges();
        }
        if (isNotEmptyOrNull(passiveInterfaces))
        {
            passiveInterfaces();
        }
        enableISIS();
        exit("config");
        advertiseInterfacesIpv4();
        advertiseInterfacesIpv6();
        end();
    }
}
