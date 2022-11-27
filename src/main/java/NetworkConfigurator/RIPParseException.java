package main.java.NetworkConfigurator;

public class RIPParseException extends NoIPv4AddressFoundException
{
    public RIPParseException(String message)
    {
        super(message);
    }
}
