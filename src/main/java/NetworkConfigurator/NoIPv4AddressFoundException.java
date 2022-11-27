package main.java.NetworkConfigurator;

public class NoIPv4AddressFoundException extends RuntimeException
{
    public NoIPv4AddressFoundException(String message)
    {
        super(message);
    }
}
