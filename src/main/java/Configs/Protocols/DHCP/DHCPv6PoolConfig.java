package main.java.Configs.Protocols.DHCP;


import main.java.BasicNetworking.IPv6Network;
import main.java.Configs.Config;

public class DHCPv6PoolConfig extends Config
{

    private final String poolName;
    private IPv6Network network;
    private final IPv6Network dnsServer;
    private String domainName;
    private boolean isFullDhcp;

    public DHCPv6PoolConfig(String poolName, IPv6Network dnsServer)
    {
        this.poolName = poolName;
        this.dnsServer = dnsServer;
    }

    public DHCPv6PoolConfig withAddressPrefix(IPv6Network addresses)
    {
        this.network = addresses;
        return this;
    }

    public DHCPv6PoolConfig withDomainName(String domainName)
    {
        this.domainName = domainName;
        return this;
    }

    private void addressPrefix()
    {
        send("address prefix " + network.getIPAddress() + network.getPrefix(), "config-dhcpv6");
    }

    private void specifyDomainName()
    {
        send("domain-name " + domainName, "config-dhcpv6");
    }

    private void specifyDNSServer()
    {
        send("dns-server " + dnsServer.getIPAddress(), "config-dhcpv6");
    }

    private void enterPoolSubConfig()
    {
        send("ipv6 dhcp pool " + poolName, "config-dhcpv6");
    }


    @Override
    public void work()
    {
        logger.write("-- BEGIN DHCPv6 POOL CONFIG --");
        configure();
        enterPoolSubConfig();
        if (isNotEmptyOrNull(network))
        {
            addressPrefix();
            specifyDNSServer();
            isFullDhcp = true;
        }
        else
        {
            specifyDNSServer();
            isFullDhcp = false;
        }
        if (isNotEmptyOrNull(domainName))
        {
            specifyDomainName();
        }
        end();
    }
}
