package main.java.Configs.Protocols.Utility;

import main.java.Configs.Config;

public class ServiceConfig extends Config
{
    private boolean ipv6Unicast;
    private boolean timeStamps;
    private boolean passwordEncryption;
    private boolean lineNumber;
    private String hostname;
    private String password;
    private boolean secret = true;


    public ServiceConfig withIpv6UnicastRouting(boolean unicastRouting)
    {
        this.ipv6Unicast = unicastRouting;
        return this;
    }

    public ServiceConfig withTimestamps(boolean timeStamps)
    {
        this.timeStamps = timeStamps;
        return this;
    }

    public ServiceConfig withPasswordEncryption(boolean passwordEncryption)
    {
        this.passwordEncryption = passwordEncryption;
        return this;
    }

    public ServiceConfig withLineNumber(boolean lineNumber)
    {
        this.lineNumber = lineNumber;
        return this;
    }

    public ServiceConfig withHostname(String hostname)
    {
        this.hostname = hostname;
        return this;
    }

    public ServiceConfig withEnablePassword(String password, boolean secret)
    {
        this.password = password;
        this.secret = secret;
        return this;
    }

    private void enableIpv6UnicastRouting()
    {
        send("ipv6 unicast-routing", "config");
    }

    private void enableLoggingTimestamps()
    {
        send("service timestamps log datetime msec", "config");
    }

    private void enablePasswordEncryption()
    {
        send("service password-encryption", "config");
    }

    private void enableLineNumber()
    {
        send("service linenumber", "config");
    }

    private void setHostname()
    {
        send("hostname " + hostname, "config");
    }

    private void enablePassword()
    {
        send("enable password " + password, "config");
    }

    private void enableSecretPassword()
    {
        send("enable secret " + password, "config");
    }

    @Override
    public void work()
    {
        if (ipv6Unicast || timeStamps || passwordEncryption || lineNumber || isNotEmptyOrNull(hostname) || isNotEmptyOrNull(password))
        {
            logger.write("-- BEGIN SERVICE CONFIG --");
            configure();
            if (ipv6Unicast)
            {
                enableIpv6UnicastRouting();
            }
            if (timeStamps)
            {
                enableLoggingTimestamps();
            }
            if (passwordEncryption)
            {
                enablePasswordEncryption();
            }
            if (lineNumber)
            {
                enableLineNumber();
            }
            if (isNotEmptyOrNull(hostname))
            {
                setHostname();
            }
            if (isNotEmptyOrNull(password))
            {
                if (secret)
                {
                    enableSecretPassword();
                }
                else
                {
                    enablePassword();
                }
            }
            end();
        }
    }
}
