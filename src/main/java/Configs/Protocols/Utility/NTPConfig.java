package main.java.Configs.Protocols.Utility;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.IPv6Network;
import main.java.Configs.Config;

import java.util.ArrayList;

public class NTPConfig extends Config
{
    private boolean updateCalendar;
    private ArrayList<IPv4Network> ntpServersV4 = new ArrayList<>();
    private ArrayList<IPv6Network> ntpServersV6 = new ArrayList<>();

    public NTPConfig withIpv4ServerAddress(IPv4Network server)
    {
        this.ntpServersV4.add(server);
        return this;
    }

    public NTPConfig withIpv6ServerAddress(IPv6Network server)
    {
        this.ntpServersV6.add(server);
        return this;
    }

    public NTPConfig withUpdateCalendar(boolean updateCalendar)
    {
        this.updateCalendar = updateCalendar;
        return this;
    }

    private void setNTPServerV4()
    {
        for (IPv4Network net : ntpServersV4)
        {
            send("ntp server " + net.getIPAddress(), "config");
        }
    }

    private void setNTPServerV6()
    {
        for (IPv6Network net : ntpServersV6)
        {
            send("ntp server " + net.getIPAddress(), "config");
        }
    }

    private void updateCalendar()
    {
        send("ntp update-calendar", "config");
    }

    @Override
    public void work()
    {
        if (isNotEmptyOrNull(ntpServersV4) || isNotEmptyOrNull(ntpServersV6) || updateCalendar)
        {
            logger.write("-- BEGIN NTP CONFIG --");
            configure();
            if (isNotEmptyOrNull(ntpServersV4))
            {
                setNTPServerV4();
            }
            if (isNotEmptyOrNull(ntpServersV6))
            {
                setNTPServerV6();
            }
            if (updateCalendar)
            {
                updateCalendar();
            }
            end();
        }
    }
}
