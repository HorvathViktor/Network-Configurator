package main.java.BasicNetworking;

public class EthernetInterface implements Interface
{
    private final EthernetTypes type;
    private final int slot;
    private final int port;


    public EthernetInterface(EthernetTypes type, int slot, int port)
    {
        this.type = type;
        this.slot = slot;
        this.port = port;
    }


    @Override
    public EthernetTypes getType()
    {
        return type;
    }

    @Override
    public String getInterfaceID()
    {
        return slot + "/" + port;
    }

    @Override
    public String getExpectedPrompt()
    {
        return "config-if";
    }

    @Override
    public String toString()
    {
        return getInterface();
    }
}
