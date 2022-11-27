package main.java.BasicNetworking;

import org.jetbrains.annotations.NotNull;

public class Subinterface implements Interface
{

    private final InterfaceType hostInterfaceType;
    private final String hostID;
    private final int vlan;

    public Subinterface(@NotNull Interface hostInterface, int vlan)
    {
        hostInterfaceType = hostInterface.getType();
        hostID = hostInterface.getInterfaceID();
        this.vlan = vlan;
    }

    @Override
    public InterfaceType getType()
    {
        return hostInterfaceType;
    }

    @Override
    public String getInterfaceID()
    {
        return hostID + "." + vlan;
    }

    @Override
    public String getExpectedPrompt()
    {
        return "config-subif";
    }

    @Override
    public String toString()
    {
        return getInterface();
    }

    public int getVlan()
    {
        return vlan;
    }
}
