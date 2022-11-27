package main.java.BasicNetworking;

public class LogicalInterface implements Interface
{
    private final LogicalInterfaces type;
    private final int interfaceID;

    public LogicalInterface(LogicalInterfaces type, int interfaceID)
    {
        this.type = type;
        this.interfaceID = interfaceID;
    }

    @Override
    public InterfaceType getType()
    {
        return this.type;
    }

    @Override
    public String getInterfaceID()
    {
        return String.valueOf(this.interfaceID);
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
