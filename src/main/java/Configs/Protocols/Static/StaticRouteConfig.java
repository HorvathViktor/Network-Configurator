package main.java.Configs.Protocols.Static;

import main.java.BasicNetworking.IPNetwork;
import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class StaticRouteConfig extends Config
{

    private final IPNetwork network;
    private IPNetwork nextHop;
    private Interface iface;
    private int administrativeDistance = 1;

    public StaticRouteConfig(IPNetwork network)
    {
        this.network = network;
    }

    public StaticRouteConfig withNextHopNetwork(IPNetwork nextHop)
    {
        this.nextHop = nextHop;
        return this;
    }

    public StaticRouteConfig withInterface(Interface iface)
    {
        this.iface = iface;
        return this;
    }

    public StaticRouteConfig withAdministrativeDistance(int administrativeDistance)
    {
        this.administrativeDistance = administrativeDistance;
        return this;
    }

    private void ipRouteNextHop()
    {
        send("ip route " + network.getIPAddress() + " " + ((IPv4Network) network).getMask() + " " + nextHop.getIPAddress() +" "+ administrativeDistance, "config");
    }

    private void ipRouteInterface()
    {
        send("ip route " + network.getIPAddress() + " " + ((IPv4Network) network).getMask() + " " + iface.getInterface() +" "+ administrativeDistance, "config");
    }

    private void ipv6RouteNextHop()
    {
        send("ipv6 route " + network.getIPAddress() + ((IPv6Network) network).getPrefix() + " " + nextHop.getIPAddress() +" "+ administrativeDistance, "config");
    }

    private void ipv6RouteInterface()
    {
        send("ipv6 route " + network.getIPAddress() + ((IPv6Network) network).getPrefix() + " " + iface.getInterface() +" "+ administrativeDistance, "config");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN STATIC ROUTES CONFIG --");
        if (isNotEmptyOrNull(nextHop) && !network.getClass().equals(nextHop.getClass()))
        {
            throw new IllegalArgumentException("Static route destination network and next hop were not from the same address family");
        }
        configure();
        if (isNotEmptyOrNull(nextHop))
        {
            if (network instanceof IPv4Network)
            {
                checkv4AdminDistance();
                ipRouteNextHop();
            }
            else
            {
                checkv6AdminDistance();
                ipv6RouteNextHop();
            }
        }
        else
        {
            if (network instanceof IPv4Network)
            {
                checkv4AdminDistance();
                ipRouteInterface();
            }
            else
            {
                checkv6AdminDistance();
                ipv6RouteInterface();
            }
        }
        end();
    }

    private void checkv4AdminDistance()
    {
        if (administrativeDistance > 255 || administrativeDistance < 1)
        {
            throw new IllegalArgumentException("IPv4 static route administrative distance was incorrect");
        }
    }

    private void checkv6AdminDistance()
    {
        if (administrativeDistance > 254 || administrativeDistance < 1)
        {
            throw new IllegalArgumentException("IPv6 static route administrative distance was incorrect");
        }
    }
}
