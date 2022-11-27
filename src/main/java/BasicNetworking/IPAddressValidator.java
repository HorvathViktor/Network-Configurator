package main.java.BasicNetworking;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.net.util.SubnetUtils;

public class IPAddressValidator
{
    private static final InetAddressValidator validator = InetAddressValidator.getInstance();

    public static void isValid(String IPAddress)
    {
        try
        {
            isValidIPv4(IPAddress);
        }
        catch (InvalidIPException e)
        {
            try
            {
                isValidIPv6(IPAddress);
            }
            catch (InvalidIPException ex)
            {
                throw e;
            }
        }
    }

    public static void isValidIPv4(String IPv4Address)
    {
        if (!validator.isValidInet4Address(IPv4Address))
        {
            throw new InvalidIPException("The input " + IPv4Address + " was not a valid IPv4 address");
        }
    }

    public static void isValidIPv6(String IPv6Address)
    {
        if (!validator.isValidInet6Address(IPv6Address))
        {
            throw new InvalidIPException("The input " + IPv6Address + " was not a valid IPv6 address");
        }
    }

    public static void isValidIpv4Network(String iPv4Address, String mask)
    {
        SubnetUtils subnetUtils = new SubnetUtils(iPv4Address, mask);
    }
    public static void isValidIpv6Network(String iPv6Address, int prefix)
    {
        if(0 <= prefix && 128 >= prefix)
        {
            isValidIPv6(iPv6Address);
        }
        else
        {
            throw new InvalidIPException("The prefix of the IPv6 mask was not valid");
        }
    }
    public static String convertIpv4PrefixToMask(String prefix)
    {
        return switch (prefix)
                {
                    case "32" -> "255.255.255.255";
                    case "31" -> "255.255.255.254";
                    case "30" -> "255.255.255.252";
                    case "29" -> "255.255.255.248";
                    case "28" -> "255.255.255.240";
                    case "27" -> "255.255.255.224";
                    case "26" -> "255.255.255.192";
                    case "25" -> "255.255.255.128";
                    case "24" -> "255.255.255.0";
                    case "23" -> "255.255.254.0";
                    case "22" -> "255.255.252.0";
                    case "21" -> "255.255.248.0";
                    case "20" -> "255.255.240.0";
                    case "19" -> "255.255.224.0";
                    case "18" -> "255.255.192.0";
                    case "17" -> "255.255.128.0";
                    case "16" -> "255.255.0.0";
                    case "15" -> "255.254.0.0";
                    case "14" -> "255.252.0.0";
                    case "13" -> "255.248.0.0";
                    case "12" -> "255.240.0.0";
                    case "11" -> "255.224.0.0";
                    case "10" -> "255.192.0.0";
                    case "9" -> "255.128.0.0";
                    case "8" -> "255.0.0.0";
                    case "7" -> "254.0.0.0";
                    case "6" -> "252.0.0.0";
                    case "5" -> "248.0.0.0";
                    case "4" -> "240.0.0.0";
                    case "3" -> "224.0.0.0";
                    case "2" -> "192.0.0.0";
                    case "1" -> "128.0.0.0";
                    case "0" -> "0.0.0.0";
                    default -> throw new IllegalArgumentException("Invalid prefix");
                };
    }
}
