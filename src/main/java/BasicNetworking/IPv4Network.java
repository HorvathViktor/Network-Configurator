package main.java.BasicNetworking;

import org.jetbrains.annotations.NotNull;

public final class IPv4Network implements IPNetwork
{
    private final String IPAddress;
    private final String mask;
    private final String prefix;
    private final String wildcard;

    public IPv4Network(String IPAddress, @NotNull String mask)
    {
        this.IPAddress = IPAddress;
        if (mask.split("\\.").length == 4)
        {
            this.mask = mask;
            this.prefix = setPrefix();
        }
        else
        {
            if (mask.contains("/"))
            {
                mask = mask.substring(1);
            }
            this.prefix = mask;
            this.mask = setMask();
        }
        this.wildcard = setWildcardMask();
    }

    public @NotNull String getIPAddress()
    {
        return IPAddress;
    }

    public String getMask()
    {
        return mask;
    }

    public @NotNull String getPrefix()
    {
        return "/" + prefix;
    }

    public String getWildcard()
    {
        return wildcard;
    }

    private String setWildcardMask()
    {
        return switch (mask)
                {
                    case "255.255.255.255" -> "0.0.0.0";
                    case "255.255.255.254" -> "0.0.0.1";
                    case "255.255.255.252" -> "0.0.0.3";
                    case "255.255.255.248" -> "0.0.0.7";
                    case "255.255.255.240" -> "0.0.0.15";
                    case "255.255.255.224" -> "0.0.0.31";
                    case "255.255.255.192" -> "0.0.0.63";
                    case "255.255.255.128" -> "0.0.0.127";
                    case "255.255.255.0" -> "0.0.0.255";
                    case "255.255.254.0" -> "0.0.1.255";
                    case "255.255.252.0" -> "0.0.3.255";
                    case "255.255.248.0" -> "0.0.7.255";
                    case "255.255.240.0" -> "0.0.15.255";
                    case "255.255.224.0" -> "0.0.31.255";
                    case "255.255.192.0" -> "0.0.63.255";
                    case "255.255.128.0" -> "0.0.127.255";
                    case "255.255.0.0" -> "0.0.255.255";
                    case "255.254.0.0" -> "0.1.255.255";
                    case "255.252.0.0" -> "0.3.255.255";
                    case "255.248.0.0" -> "0.7.255.255";
                    case "255.240.0.0" -> "0.15.255.255";
                    case "255.224.0.0" -> "0.31.255.255";
                    case "255.192.0.0" -> "0.63.255.255";
                    case "255.128.0.0" -> "0.127.255.255";
                    case "255.0.0.0" -> "0.255.255.255";
                    case "254.0.0.0" -> "1.255.255.255";
                    case "252.0.0.0" -> "3.255.255.255";
                    case "248.0.0.0" -> "7.255.255.255";
                    case "240.0.0.0" -> "15.255.255.255";
                    case "224.0.0.0" -> "31.255.255.255";
                    case "192.0.0.0" -> "63.255.255.255";
                    case "128.0.0.0" -> "127.255.255.255";
                    case "0.0.0.0" -> "255.255.255.255";
                    default -> null;
                };
    }

    private String setPrefix()
    {
        return switch (mask)
                {
                    case "255.255.255.255" -> "32";
                    case "255.255.255.254" -> "31";
                    case "255.255.255.252" -> "30";
                    case "255.255.255.248" -> "29";
                    case "255.255.255.240" -> "28";
                    case "255.255.255.224" -> "27";
                    case "255.255.255.192" -> "26";
                    case "255.255.255.128" -> "25";
                    case "255.255.255.0" -> "24";
                    case "255.255.254.0" -> "23";
                    case "255.255.252.0" -> "22";
                    case "255.255.248.0" -> "21";
                    case "255.255.240.0" -> "20";
                    case "255.255.224.0" -> "19";
                    case "255.255.192.0" -> "18";
                    case "255.255.128.0" -> "17";
                    case "255.255.0.0" -> "16";
                    case "255.254.0.0" -> "15";
                    case "255.252.0.0" -> "14";
                    case "255.248.0.0" -> "13";
                    case "255.240.0.0" -> "12";
                    case "255.224.0.0" -> "11";
                    case "255.192.0.0" -> "10";
                    case "255.128.0.0" -> "9";
                    case "255.0.0.0" -> "8";
                    case "254.0.0.0" -> "7";
                    case "252.0.0.0" -> "6";
                    case "248.0.0.0" -> "5";
                    case "240.0.0.0" -> "4";
                    case "224.0.0.0" -> "3";
                    case "192.0.0.0" -> "2";
                    case "128.0.0.0" -> "1";
                    case "0.0.0.0" -> "0";
                    default -> null;
                };
    }

    private String setMask()
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
                    default -> null;
                };
    }
}
