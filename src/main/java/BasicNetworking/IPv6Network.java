package main.java.BasicNetworking;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class IPv6Network implements IPNetwork
{
    private String IPAddress;
    private String prefix;
    private String linkLocal;

    public IPv6Network(String IPAddress, String prefix, String linkLocal)
    {
        this.IPAddress = IPAddress;
        this.prefix = prefix;
        this.linkLocal = linkLocal;
    }

    public IPv6Network(String IPAddress, String prefix)
    {
        this.IPAddress = IPAddress;
        this.prefix = prefix;
    }

    public IPv6Network(String linkLocal)
    {
        this.linkLocal = linkLocal;
    }

    public @Nullable String getLinkLocalAddress()
    {
        return linkLocal;
    }

    public @Nullable String getIPAddress()
    {
        return IPAddress;
    }

    public void setLinkLocal(String linkLocal)
    {
        this.linkLocal = linkLocal;
    }


    @Contract(pure = true)
    public String getPrefix()
    {
        return "/" + prefix;
    }
}
