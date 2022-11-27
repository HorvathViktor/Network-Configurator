package main.java.Configs.Protocols.Redundancy;


import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public class VRRPConfig extends Config
{
    protected boolean overthrow = true;
    private Interface iface;
    private int group;
    private int priority = -1;
    private IPv4Network virtualIP;

    public VRRPConfig(Interface iface, int group, IPv4Network virtualIP)
    {
        this.iface = iface;
        if (group < 1 || group > 255)
        {
            throw new IllegalArgumentException("VRRP priority is incorrect");
        }
        this.group = group;
        this.virtualIP = virtualIP;
    }

    public VRRPConfig withPreempt(boolean preempt)
    {
        this.overthrow = preempt;
        return this;
    }
    public VRRPConfig withPriority(int priority)
    {
        if (priority < 1 || priority > 254)
        {
            throw new IllegalArgumentException("VRRP priority is incorrect");
        }
        this.priority = priority;
        return this;
    }
    private void addVirtualIP()
    {
        send("vrrp " + group + " ip " + virtualIP.getIPAddress(), iface.getExpectedPrompt());
    }

    private void preempt()
    {
        send("vrrp " + group + " preempt", iface.getExpectedPrompt());
    }

    private void setPriority()
    {
        send("vrrp " + group + " priority " + priority, iface.getExpectedPrompt());
    }

    private void noShutdown()
    {
        send("no vrrp " + group + " shutdown", iface.getExpectedPrompt());
    }


    @Override
    public void work()
    {
        logger.write("-- BEGIN VRRP CONFIG --");
        configure();
        enterInterface(iface);
        addVirtualIP();
        if(priority != -1)
        {
            setPriority();
        }
        if(overthrow)
        {
            preempt();
        }
        noShutdown();
        end();
    }
}
