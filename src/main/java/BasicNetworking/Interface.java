package main.java.BasicNetworking;

public interface Interface
{
    InterfaceType getType();

    String getInterfaceID();

    default String getInterface()
    {
        return getType() + getInterfaceID();
    }
    String getExpectedPrompt();
}