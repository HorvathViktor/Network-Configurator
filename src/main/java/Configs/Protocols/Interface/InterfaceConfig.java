package main.java.Configs.Protocols.Interface;


import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class InterfaceConfig extends Config
{

    protected final Interface iface;
    protected boolean adminStatus;

    public InterfaceConfig(Interface iface)
    {
        this.iface = iface;
    }

    public InterfaceConfig withAdminStatus(boolean adminStatus)
    {
        this.adminStatus = adminStatus;
        return this;
    }

    protected void bringUp()
    {
        send("no shutdown", "config-if");
    }

    protected void shutdown()
    {
        send("shutdown", "config-if");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN INTERFACE CONFIG --");
        configure();
        enterInterface(iface);
        if(adminStatus)
        {
            bringUp();
        }
        else
        {
            shutdown();
        }
        end();
    }
}

