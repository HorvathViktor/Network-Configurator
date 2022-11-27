package main.java.Configs.Protocols.DNS;


import main.java.BasicNetworking.IPNetwork;
import main.java.Configs.Config;

import java.util.ArrayList;

public class DNSConfig extends Config
{
    private boolean lookup = false;
    private String routerDomainName;
    private ArrayList<IPNetwork> dnsServerAddresses = new ArrayList<>();


    public DNSConfig withLookup(boolean lookup)
    {
        this.lookup = lookup;
        return this;
    }

    public DNSConfig withDnsServer(IPNetwork dnsServerAddress)
    {
        this.dnsServerAddresses.add(dnsServerAddress);
        return this;
    }

    public DNSConfig withDomainName(String routerDomainName)
    {
        this.routerDomainName = routerDomainName;
        return this;
    }

    private void specifyDNSServer()
    {
        for (IPNetwork network : dnsServerAddresses)
        {
            send("ip name-server " + network.getIPAddress(), "config");
        }
    }

    private void allowDNSLookups()
    {
        send("ip domain-lookup", "config");
    }

    private void specifyDomainName()
    {
        send("ip domain-name " + routerDomainName, "config");
    }

    @Override
    public void work()
    {
        if (lookup || isNotEmptyOrNull(routerDomainName) || isNotEmptyOrNull(dnsServerAddresses))
        {
            logger.write("-- BEGIN DNS CONFIG --");
            configure();
            if (lookup)
            {
                allowDNSLookups();
            }
            if (isNotEmptyOrNull(routerDomainName))
            {
                specifyDomainName();
            }
            if (isNotEmptyOrNull(dnsServerAddresses))
            {
                specifyDNSServer();
            }
            end();
        }
    }
}
