package main.java.Configs.Protocols.Redundancy;


import main.java.BasicNetworking.Interface;
import main.java.Configs.Config;

public abstract class HSRPConfig extends Config
{
    protected boolean overthrow = true;
    protected boolean version2 = true;
    protected Interface iface;
    protected int group;
    protected int priority = -1;

    public HSRPConfig(Interface iface, int group)
    {
        this.iface = iface;
        if (group > 255 || group < 0)
        {
            throw new IllegalArgumentException("HSRP group number is incorrect");
        }
        this.group = group;
    }

    public HSRPConfig withPreempt(boolean preempt)
    {
        this.overthrow = preempt;
        return this;
    }

    public HSRPConfig withVersion2(boolean version2)
    {
        this.version2 = version2;
        return this;
    }

    public HSRPConfig withPriority(int priority)
    {
        if (priority < 0 || priority > 255)
        {
            throw new IllegalArgumentException("HSRP priority is incorrect");
        }
        this.priority = priority;
        return this;
    }

    protected void preempt()
    {
        send("standby " + group + " preempt", iface.getExpectedPrompt());
    }

    protected void setPriority()
    {
        send("standby " + group + " priority " + priority, iface.getExpectedPrompt());
    }

    protected void configureHSRPVersion2()
    {
        send("standby version 2", iface.getExpectedPrompt());
    }
}
