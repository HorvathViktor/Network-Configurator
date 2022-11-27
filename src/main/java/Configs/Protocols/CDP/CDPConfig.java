package main.java.Configs.Protocols.CDP;

import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class CDPConfig extends Config
{
    private Interface sourceInterface;
    private boolean v2 = true;
    private boolean globallyEnabled = true;
    private boolean log = true;

    private CDPInterfaces[] interfaces;

    public record CDPInterfaces(Interface iface, boolean enabled)
    {

    }

    public CDPConfig withV2(boolean v2)
    {
        this.v2 = v2;
        return this;
    }

    public CDPConfig withEnabled(boolean enabled)
    {
        this.globallyEnabled = enabled;
        return this;
    }

    public CDPConfig withMismatchLog(boolean log)
    {
        this.log = log;
        return this;
    }

    public CDPConfig withSourceInterface(Interface iface)
    {
        this.sourceInterface = iface;
        return this;
    }

    public CDPConfig withSpecificInterfaces(CDPInterfaces[] interfaces)
    {
        this.interfaces = interfaces;
        return this;
    }

    private void advertiseV2()
    {
        send("cdp advertise-v2", "config");
    }

    private void enableCDPGlobally()
    {
        send("cdp run", "config");
    }

    private void disableCDPGlobally()
    {
        send("no cdp run", "config");
    }

    private void defineSourceInterfaceForIP()
    {
        send("cdp source-interface " + sourceInterface.getType() + " " + sourceInterface.getInterfaceID(), "config");
    }

    private void logCDPMismatch()
    {
        send("cdp log mismatch duplex", "config");
    }

    private void interfaceCDP()
    {
        for (CDPInterfaces iface : interfaces)
        {
            enterInterface(iface.iface);
            if (iface.enabled)
            {
                send("cdp enable", iface.iface.getExpectedPrompt());
            }
            else
            {
                send("no cdp enable", iface.iface.getExpectedPrompt());
            }
            exit("config");
        }
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN CDP CONFIG --");
        configure();
        if (globallyEnabled)
        {
            enableCDPGlobally();
            if (v2)
            {
                advertiseV2();
            }
            if (log)
            {
                logCDPMismatch();
            }
            if (isNotEmptyOrNull(sourceInterface))
            {
                defineSourceInterfaceForIP();
            }
            if (isNotEmptyOrNull(interfaces))
            {
                interfaceCDP();
            }
        }
        else
        {
            disableCDPGlobally();
        }
        end();
    }
}
