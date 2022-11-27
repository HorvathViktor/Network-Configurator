package main.java.Configs.Protocols.DHCP;


import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

import static main.java.Configs.Protocols.DHCP.DHCPv6Config.PoolType.BOTH;
import static main.java.Configs.Protocols.DHCP.DHCPv6Config.PoolType.DHCPV6_ONLY;

public class DHCPv6Config extends Config
{

    public enum PoolType
    {
        SLAAC_ONLY, DHCPV6_ONLY, BOTH
    }

    private int validLifetime = -1;
    private int preferredLifetime = -1;
    private final Interface iface;
    private final DHCPv6Pool pool;

    public DHCPv6Config(Interface iface, DHCPv6Pool pool)
    {
        this.iface = iface;
        this.pool = pool;
    }

    public DHCPv6Config withLifetime(int validLifetime, int preferredLifetime)
    {
        this.validLifetime = validLifetime;
        this.preferredLifetime = preferredLifetime;
        return this;
    }

    public record DHCPv6Pool(String poolName, IPv6Network network, PoolType desired, PoolType actual)
    {

    }

    private void setDHCPv6OnInterface()
    {
        send("ipv6 dhcp server " + pool.poolName, iface.getExpectedPrompt());
    }

    // For DHCPv6
    private void setManagedConfigFlag()
    {
        send("ipv6 nd managed-config-flag", iface.getExpectedPrompt());
    }

    //For SLAAC
    private void setOtherConfigFlag()
    {
        send("ipv6 nd other-config-flag", iface.getExpectedPrompt());
    }

    //This kills SLAAC and makes the Other-config-flag useless
    private void turnOffAdvertisement()
    {
        send("ipv6 nd prefix " + pool.network.getIPAddress() + pool.network.getPrefix() + " no-advertise", iface.getExpectedPrompt());
    }

    //This kills SLAAC and makes the Other-config-flag useless
    private void turnOffAdvertisementWithLifetime()
    {
        send("ipv6 nd prefix " + pool.network.getIPAddress() + pool.network.getPrefix() + validLifetime + preferredLifetime + " no-advertise", iface.getExpectedPrompt());
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN DHCPv6 CONFIG --");
        configure();
        enterInterface(iface);
        if (pool.desired == DHCPV6_ONLY || pool.desired == BOTH)
        {
            if (pool.actual == DHCPV6_ONLY || pool.actual == BOTH)
            {
                setDHCPv6OnInterface();
                setManagedConfigFlag();
                if (pool.desired == DHCPV6_ONLY)
                {
                    if (validLifetime != -1 && preferredLifetime != -1)
                    {
                        turnOffAdvertisementWithLifetime();
                    }
                    else
                    {
                        turnOffAdvertisement();
                    }
                }
                else
                {
                    setOtherConfigFlag();
                }
            }
            else
            {
                throw new IncompatiblePoolException("The configured pool '" + pool.poolName + "' is an SLAAC pool and cannot be used as a Full DHCPv6 pool");
            }
        }
        else
        {
            setDHCPv6OnInterface();
            setOtherConfigFlag();
        }
        end();
    }
}
