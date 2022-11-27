package main.java.Configs.Protocols.VTY;


import main.java.Configs.Config;

public class SSHConfig extends Config
{
    private int retries = 0;
    private int timeout = 0;
    private boolean version2 = true;
    private final int bits;
    private boolean eventLogging;

    public SSHConfig(int cryptoBits)
    {
        if (cryptoBits > 4096 || cryptoBits < 360)
        {
            throw new IllegalArgumentException("Rsa bits are incorrect");
        }
        this.bits = cryptoBits;
    }

    public SSHConfig withMaximumRetries(int retries)
    {
        if (retries > 5 || retries < 0)
        {
            throw new IllegalArgumentException("SSH Authentication Retries are incorrect");
        }
        this.retries = retries;
        return this;
    }

    public SSHConfig withVersion2(boolean version2)
    {
        this.version2 = version2;
        return this;
    }

    public SSHConfig withEventLogging(boolean eventLogging)
    {
        this.eventLogging = eventLogging;
        return this;
    }

    public SSHConfig withTimeoutSeconds(int timeout)
    {
        if (timeout > 120 || timeout < 1)
        {
            throw new IllegalArgumentException("SSH timeout is incorrect");
        }
        this.timeout = timeout;
        return this;
    }

    private void setSSHMaxRetries()
    {
        send("ip ssh authentication-retries " + retries, "config");
    }

    private void setSSHTimeout()
    {
        send("ip ssh authentication-retries " + timeout, "config");
    }

    private void setSSHv2()
    {
        send("ip ssh version 2", "config");
    }

    private void generateCryptoKey()
    {

        send("crypto key generate rsa modulus " + bits, "config");
    }

    private void logSSHEvents()
    {
        send("ip ssh logging events", "config");
    }

    @Override
    public void work()
    {
        logger.write("-- BEGIN SSH CONFIG --");
        configure();
        if (version2)
        {
            setSSHv2();
        }
        generateCryptoKey();
        if (retries != 0)
        {
            setSSHMaxRetries();
        }
        if (timeout != 0)
        {
            setSSHTimeout();
        }
        if (eventLogging)
        {
            logSSHEvents();
        }
        end();
    }
}
