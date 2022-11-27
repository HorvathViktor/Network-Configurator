package main.java.Configs.Protocols.OSPF;

import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class OSPFv3Config extends Config
{
    private final int processID;
    private final String routerID;
    private boolean redistributeStatic = false;
    private OSPFv3Config.OSPFv3Network[] interfacesToAdvertise;
    private Interface[] passiveInterfaces;
    private boolean defaultInformationOriginate = false;
    private boolean logChanges = true;

    public OSPFv3Config(int processID, String routerID)
    {
        if (processID < 1 || processID > 65535)
        {
            throw new IllegalArgumentException("OSPFv3 process ID is incorrect");
        }
        this.processID = processID;
        this.routerID = routerID;
    }


    public record OSPFv3Network(Interface interfacesToAdvertise, int area)
    {
        public OSPFv3Network
        {
            if (area < 0)
            {
                throw new IllegalArgumentException("OSPF area number can't be negative");
            }
        }
    }

    public OSPFv3Config withStaticRedistribution(boolean redistributeStatic)
    {
        this.redistributeStatic = redistributeStatic;
        return this;
    }

    public OSPFv3Config withInterfacesToAdvertise(OSPFv3Config.OSPFv3Network[] interfacesToAdvertise)
    {
        this.interfacesToAdvertise = interfacesToAdvertise;
        return this;
    }

    public OSPFv3Config withPassiveInterfaces(Interface[] interfaces)
    {
        this.passiveInterfaces = interfaces;
        return this;
    }

    public OSPFv3Config withAdjacencyLogging(boolean logging)
    {
        this.logChanges = logging;
        return this;
    }

    public OSPFv3Config withDefaultRouteAdvertisement(boolean defaultRouteAdvertisement)
    {
        this.defaultInformationOriginate = defaultRouteAdvertisement;
        return this;
    }

    private void advertiseInterfaces()
    {
        for (OSPFv3Network networks : interfacesToAdvertise)
        {
            enterInterface(networks.interfacesToAdvertise);
            send("ipv6 ospf " + processID + " area " + networks.area, networks.interfacesToAdvertise.getExpectedPrompt());
            exit("config");
        }
    }

    private void passiveInterfaces()
    {
        for (Interface iface : passiveInterfaces)
        {
            send("passive-interface " + iface.getType().toString() + iface.getInterfaceID(), "config-rtr");
        }
    }

    private void redistributeStatic()
    {
        send("redistribute static", "config-rtr");
    }

    private void defaultInformationOriginate()
    {
        send("default-information originate", "config-rtr");
    }

    private void setRouterID()
    {
        send("router-id " + routerID, "config-rtr");
    }

    private void logAdjacencyChanges()
    {
        send("log-adjacency-changes detail", "config-rtr");
    }

    private void enterOSPFSubConfig()
    {

        send("ipv6 router ospf " + processID, "config-rtr");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN OSPFv3 CONFIG --");
        configure();
        enterOSPFSubConfig();
        setRouterID();
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
        exit("config");
        advertiseInterfaces();
        end();
    }
}
