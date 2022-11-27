package main.java.Configs.Protocols.DHCP;

import main.java.BasicNetworking.IPv4Network;
import main.java.Configs.Config;

public class DHCPConfig extends Config
{
    private IPv4Network excludeStart;
    private IPv4Network excludeEnd;
    private final IPv4Network poolNetwork;
    private IPv4Network defaultGateway;
    private IPv4Network dnsServer;
    private String domainName;
    private final String poolName;
    private boolean infinite = false;
    private int days = 0;
    private int hours = 8;
    private int minutes = 0;

    public DHCPConfig(IPv4Network poolNetwork, String poolName)
    {
        this.poolNetwork = poolNetwork;
        this.poolName = poolName;
    }

    public DHCPConfig withExclusionRange(IPv4Network excludeStart, IPv4Network excludeEnd)
    {
        this.excludeStart = excludeStart;
        this.excludeEnd = excludeEnd;
        return this;
    }

    public DHCPConfig withDefaultGateway(IPv4Network defaultGateway)
    {
        this.defaultGateway = defaultGateway;
        return this;
    }

    public DHCPConfig withDNSServer(IPv4Network dnsServer)
    {
        this.dnsServer = dnsServer;
        return this;
    }

    public DHCPConfig withDomainName(String domainName)
    {
        this.domainName = domainName;
        return this;
    }

    public DHCPConfig withInfiniteLease()
    {
        infinite = true;
        return this;
    }

    public DHCPConfig withSpecificLease(int days, int hours, int minutes)
    {
        if (days > 365 || days < 0)
        {
            throw new IllegalArgumentException("Days are incorrect");
        }
        if (hours > 23 || hours < 0)
        {
            throw new IllegalArgumentException("Hours are incorrect");
        }
        if (minutes > 59 || minutes < 0)
        {
            throw new IllegalArgumentException("Minutes are incorrect");
        }
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        return this;
    }

    private void excludeAddresses()
    {
        send("ip dhcp excluded-address " + excludeStart.getIPAddress() + " " + excludeEnd.getIPAddress(), "config");
    }

    private void specifyNetwork()
    {
        send("network " + poolNetwork.getIPAddress() + " " + poolNetwork.getPrefix(), "dhcp-config");
    }

    private void specifyDefaultGateway()
    {
        send("default-router " + defaultGateway.getIPAddress(), "dhcp-config");
    }

    private void specifyDnsServer()
    {
        send("dns-server " + dnsServer.getIPAddress(), "dhcp-config");
    }

    private void specifyDomainName()
    {
        send("domain-name " + domainName, "dhcp-config");
    }

    private void specifyLeaseTime()
    {
        send("lease " + days + " " + hours + " " + minutes, "dhcp-config");
    }

    private void setLeaseToInfinite()
    {
        send("lease infinite", "dhcp-config");
    }

    private void enterPoolSubConfig()
    {
        send("ip dhcp pool " + poolName, "dhcp-config");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN DHCP CONFIG --");
        configure();
        if (excludeStart != null && excludeEnd != null)
        {
            excludeAddresses();
        }
        enterPoolSubConfig();
        specifyNetwork();
        if (isNotEmptyOrNull(defaultGateway))
        {
            specifyDefaultGateway();
        }
        if (isNotEmptyOrNull(dnsServer))
        {
            specifyDnsServer();
        }
        if (isNotEmptyOrNull(domainName))
        {
            specifyDomainName();
        }
        if (infinite)
        {
            setLeaseToInfinite();
        }
        else
        {
            specifyLeaseTime();
        }
        end();

    }

}
