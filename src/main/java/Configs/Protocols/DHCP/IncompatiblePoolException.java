package main.java.Configs.Protocols.DHCP;

public class IncompatiblePoolException extends RuntimeException
{
    public IncompatiblePoolException(String message)
    {
        super(message);
    }
}
