package main.java.Configs.Protocols.Utility;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

import java.util.ArrayList;


public class SyslogConfig extends Config
{
    private ArrayList<IPv4Network> syslogServerV4 = new ArrayList<>();
    private ArrayList<IPv6Network> syslogServerV6 = new ArrayList<>();
    private Interface sourceInterface;
    private LoggingLevel trapLevel;
    private LoggingLevel monitorLevel;

    public enum LoggingLevel
    {
        EMERGENCY(0),
        ALERTS(1),
        CRITICAL(2),
        ERRORS(3),
        WARNINGS(4),
        NOTIFICATIONS(5),
        INFORMATIONAL(6),
        DEBUGGING(7);
        private final int code;

        LoggingLevel(int code)
        {
            this.code = code;
        }
    }

    public SyslogConfig withIpv4SyslogServer(IPv4Network syslogServer)
    {
        this.syslogServerV4.add(syslogServer);
        return this;
    }

    public SyslogConfig withIpv6SyslogServer(IPv6Network syslogServer)
    {
        this.syslogServerV6.add(syslogServer);
        return this;
    }

    public SyslogConfig withSourceInterface(Interface sourceInterface)
    {
        this.sourceInterface = sourceInterface;
        return this;
    }

    public SyslogConfig withTrap(LoggingLevel trapLevel)
    {
        this.trapLevel = trapLevel;
        return this;
    }

    public SyslogConfig withMonitor(LoggingLevel monitorLevel)
    {
        this.monitorLevel = monitorLevel;
        return this;
    }

    private void setSyslogServerV4()
    {
        for (IPv4Network net : syslogServerV4)
        {
            send("logging host " + net.getIPAddress(), "config");
        }
    }

    private void setSyslogServerV6()
    {
        for (IPv6Network net : syslogServerV6)
        {
            send("logging host ipv6 " + net.getIPAddress(), "config");
        }
    }

    private void setSourceInterface()
    {
        send("logging source-interface " + sourceInterface.getType() + sourceInterface.getInterfaceID(), "config");
    }

    private void setTrap()
    {
        send("logging trap " + trapLevel.code, "config");
    }

    private void setMonitor()
    {
        send("logging monitor " + monitorLevel.code, "config");
    }

    private void turnOn()
    {
        send("logging on", "config");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN SYSLOG CONFIG --");
        configure();
        if (isNotEmptyOrNull(syslogServerV4))
        {
            setSyslogServerV4();
        }
        if (isNotEmptyOrNull(syslogServerV6))
        {
            setSyslogServerV6();
        }
        if (trapLevel != null)
        {
            setTrap();
        }
        if (monitorLevel != null)
        {
            setMonitor();
        }
        if (isNotEmptyOrNull(sourceInterface))
        {
            setSourceInterface();
        }
        turnOn();
        end();
    }
}
