package main.java.Configs.Protocols.VTY;


import main.java.Configs.Config;

public class VirtualLineConfig extends Config
{

    private final int firstVty;
    private final int lastVty;
    private boolean enableTelnet = true;
    private boolean enableSSH = true;
    private boolean execBanner = true;
    private boolean motdBanner = true;
    private int timeout = 0;

    public VirtualLineConfig(int firstVty, int lastVty)
    {
        this.firstVty = firstVty;
        this.lastVty = lastVty;
    }

    public VirtualLineConfig withTelnet(boolean telnet)
    {
        this.enableTelnet = telnet;
        return this;
    }

    public VirtualLineConfig withSSH(boolean ssh)
    {
        this.enableSSH = ssh;
        return this;
    }

    public VirtualLineConfig withExecBanner(boolean execBanner)
    {
        this.execBanner = execBanner;
        return this;
    }

    public VirtualLineConfig withMotdBanner(boolean motdBanner)
    {
        this.motdBanner = motdBanner;
        return this;
    }

    public VirtualLineConfig withExecTimeout(int timeout)
    {
        if (timeout > 35791 || timeout < 0)
        {
            throw new IllegalArgumentException("Exec timeout is incorrect.");
        }
        this.timeout = timeout;
        return this;
    }

    private void enableOnlyTelnetOnVty()
    {
        send("transport input telnet", "config-line");
    }

    private void enableOnlySSHOnVty()
    {
        send("transport input ssh", "config-line");
    }

    private void enableTelnetAndSSHOnVty()
    {
        send("transport input telnet ssh", "config-line");
    }

    private void setLoginToLocal()
    {
        send("login local", "config-line");
    }

    private void enableExecBanner()
    {
        send("exec-banner", "config-line");
    }

    private void setExecTimeout()
    {

        send("exec-timeout " + timeout, "config-line");
    }

    private void enableMOTDBannerOnVty()
    {
        send("motd-banner", "config-line");
    }

    private void enterLineSubConfig()
    {
        send("line vty " + firstVty + " " + lastVty, "config-line");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN VTY CONFIG --");
        configure();
        enterLineSubConfig();
        setLoginToLocal();
        if (motdBanner)
        {
            enableMOTDBannerOnVty();
        }
        if (execBanner)
        {
            enableExecBanner();
        }
        if (timeout != 0)
        {
            setExecTimeout();
        }
        if (enableTelnet)
        {
            if (enableSSH)
            {
                enableTelnetAndSSHOnVty();
            }
            else
            {
                enableOnlyTelnetOnVty();
            }
        }
        else
        {
            if (enableSSH)
            {
                enableOnlySSHOnVty();
            }
        }
        end();
    }
}
