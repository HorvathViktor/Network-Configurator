package main.java.Configs.Protocols.OSPF;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class OSPFv2Config extends Config
{
    private String routerID;
    private final int processID;
    private boolean redistributeStatic = false;
    private boolean defaultInformationOriginate = false;
    private OSPFv2Network[] networks;
    private Interface[] interfaces;
    private boolean logChanges = true;

    public record OSPFv2Network(IPv4Network networkToAdvertise, int area)
    {
        public OSPFv2Network
        {
            if (area < 0)
            {
                throw new IllegalArgumentException("OSPF area number can't be negative");
            }
        }
    }

    public OSPFv2Config(int processID)
    {
        if (processID < 1 || processID > 65535)
        {
            throw new IllegalArgumentException("OSPFv2 process ID is incorrect");
        }
        this.processID = processID;
    }

    public OSPFv2Config withRouterID(String routerID)
    {
        this.routerID = routerID;
        return this;
    }

    public OSPFv2Config withStaticRedistribution(boolean redistributeStatic)
    {
        this.redistributeStatic = redistributeStatic;
        return this;
    }

    public OSPFv2Config withNetworks(OSPFv2Network[] networks)
    {
        this.networks = networks;
        return this;
    }

    public OSPFv2Config withPassiveInterfaces(Interface[] interfaces)
    {
        this.interfaces = interfaces;
        return this;
    }

    public OSPFv2Config withAdjacencyLogging(boolean logging)
    {
        this.logChanges = logging;
        return this;
    }

    public OSPFv2Config withDefaultRouteAdvertisement(boolean defaultRouteAdvertisement)
    {
        this.defaultInformationOriginate = defaultRouteAdvertisement;
        return this;
    }

    private void advertiseNetworks()
    {
        for (OSPFv2Network network : networks)
        {
            send("network " + network.networkToAdvertise.getIPAddress() + " " +
                    network.networkToAdvertise.getWildcard() + " area " + network.area, "config-router");
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

    private void defaultInformationOriginate()
    {
        send("default-information originate", "config-router");
    }

    private void setRouterID()
    {
        send("router-id " + routerID, "config-router");
    }

    private void noShutdown()
    {
        send("no shutdown", "config-router");
    }

    private void logAdjacencyChanges()
    {
        send("log-adjacency-changes detail", "config-router");
    }

    private void enterOSPFSubConfig()
    {
        send("router ospf " + processID, "config-router");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN OSPFv2 CONFIG --");
        configure();
        enterOSPFSubConfig();
        if (isNotEmptyOrNull(routerID))
        {
            setRouterID();
        }
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
        if (isNotEmptyOrNull(networks))
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
