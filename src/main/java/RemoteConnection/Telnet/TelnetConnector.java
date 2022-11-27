package main.java.RemoteConnection.Telnet;

import main.java.RemoteConnection.Connection;
import org.apache.commons.net.telnet.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.SocketException;
import java.util.Random;

public class TelnetConnector implements Connection, AutoCloseable
{
    private final TelnetClient telnet;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final char[] chars = new char[1024];

    public TelnetConnector()
    {
        telnet = new TelnetClient("VT15");
        registerOptionHandlers();
    }

    //GNS3 connects to console port, no need for any Telnet option handler
    private void registerOptionHandlers()
    {
        final TerminalTypeOptionHandler terminalType = new TerminalTypeOptionHandler(null, false, false, false, false);
        final EchoOptionHandler echoOption = new EchoOptionHandler(false, false, false, false);
        final SuppressGAOptionHandler GAOption = new SuppressGAOptionHandler(false, false, false, false);

        try
        {
            telnet.addOptionHandler(terminalType);
            telnet.addOptionHandler(echoOption);
            telnet.addOptionHandler(GAOption);
        }
        catch (final InvalidTelnetOptionException | IOException e)
        {
            System.err.println("Error registering option handlers: " + e.getMessage());
        }
    }

    @Override
    public void connect(String destinationIP, int destinationPort) throws IOException
    {
        try
        {
            telnet.connect(destinationIP, destinationPort);
            writer = new BufferedWriter(new OutputStreamWriter(telnet.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(telnet.getInputStream()));

            writer.write("\n");
            writer.flush();

            Random rnd = new Random();
            final String marker = "" + Math.abs(rnd.nextLong()) + Math.abs(rnd.nextLong());
            writer.write(marker);
            writer.flush();

            StringBuilder soFar = new StringBuilder();
            int i;
            while ((i = reader.read(chars)) > 0)
            {
                soFar.append(chars, 0, i);
                if (soFar.indexOf(marker) != -1)
                {
                    break;
                }
            }

            writer.write(3); //End-of-Text, ctrl+c
            writer.flush();
            reader.readLine(); //Consume the new line after the interrupt
            //At this pont we should see something like 'R1#'
        }
        catch (SocketException e)
        {
            throw new IOException("Connection to: "+ destinationIP+":"+destinationPort+" was unsuccessful");
        }
    }

    @Override
    public void send(@NotNull String command)
    {
        try
        {
            writer.write(command + "\n");
            writer.flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException();
        }
    }

    @Override
    public void disconnect()
    {
        try
        {
            telnet.disconnect();
        }
        catch (final IOException e)
        {
            System.err.println("Exception while closing telnet:" + e.getMessage());
        }
    }

    @Override
    public String popPrompt()
    {
        try
        {
            reader.readLine(); // Read the command we just sent

            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1)
            {
                builder.append((char) c);
                if (c == '#')
                {
                    break;
                }
            }
            return builder.toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close()
    {
        disconnect();
    }
}

