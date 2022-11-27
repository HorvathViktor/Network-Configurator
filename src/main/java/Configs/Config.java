package main.java.Configs;


import main.java.BasicNetworking.IPNetwork;
import main.java.BasicNetworking.Interface;
import main.java.Logging.FileLogger;
import main.java.RemoteConnection.Connection;
import org.awaitility.Awaitility;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public abstract class Config
{
    private Connection connection;
    protected FileLogger logger;

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    public void setLogger(FileLogger logger)
    {
        this.logger = logger;
    }

    protected void send(String command, String expectedPrompt)
    {
        logger.write("Sending: " + command);
        connection.send(command);
        Awaitility.await()
                .atMost(1, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> CheckPrompt.assertPrompt(connection.popPrompt(), expectedPrompt, logger));
    }

    protected void configure()
    {
        send("configure terminal", "config");
    }

    protected void enterInterface(@NotNull Interface iface)
    {
        send("interface " + iface.getType() + " " + iface.getInterfaceID(), iface.getExpectedPrompt());
    }

    protected void end()
    {
        send("end", "#");
    }

    protected void exit(String expectedPrompt)
    {
        send("exit", expectedPrompt);
    }

    protected boolean isNotEmptyOrNull(IPNetwork network)
    {
        if (network == null)
        {
            return false;
        }
        if (!isNotEmptyOrNull(network.getIPAddress()) || !isNotEmptyOrNull(network.getIPAddress()))
        {
            return false;
        }
        return true;
    }

    protected boolean isNotEmptyOrNull(Interface iface)
    {
        if (iface == null)
        {
            return false;
        }
        if (!isNotEmptyOrNull(iface.getInterface()))
        {
            return false;
        }
        return true;
    }

    protected boolean isNotEmptyOrNull(Object[] array)
    {
        if (array == null)
        {
            return false;
        }
        return true;
    }

    protected boolean isNotEmptyOrNull(String string)
    {
        if (string == null)
        {
            return false;
        }
        if (string.isEmpty() || string.isBlank())
        {
            return false;
        }
        return true;
    }

    protected boolean isNotEmptyOrNull(ArrayList<? extends IPNetwork> list)
    {
        if (list == null)
        {
            return false;
        }
        if (list.size() == 0)
        {
            return false;
        }
        return true;
    }

    public abstract void work();
}
