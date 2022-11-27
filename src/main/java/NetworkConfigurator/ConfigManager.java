package main.java.NetworkConfigurator;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import main.java.Configs.Config;
import main.java.Logging.FileLogger;
import main.java.RemoteConnection.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager implements Runnable
{

    private final ArrayList<Config> configs = new ArrayList<>();
    private final Connection connection;
    private final String destinationIP;
    private final int destinationPort;
    private final ProgressBar progressBar;
    private final ListView<String> list;
    private final String routerHostname;

    public ConfigManager(Connection connection, String destinationIP, int destinationPort, ProgressBar progressBar, ListView<String> list, String routerHostname)
    {
        this.connection = connection;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.progressBar = progressBar;
        this.list = list;
        this.routerHostname = routerHostname;
    }

    public void addConfig(Config config)
    {
        configs.add(config);
    }

    public void addConfigs(Config... configs)
    {
        this.configs.addAll(List.of(configs));
    }

    @Override
    public void run()
    {
        try
        {
            connection.connect(destinationIP, destinationPort);
            FileLogger logger = new FileLogger(routerHostname);
            for (int i = 0; i < configs.size(); i++)
            {
                configs.get(i).setConnection(connection);
                configs.get(i).setLogger(logger);
                progressBar.setProgress((double) i / configs.size());
                configs.get(i).work();
            }
            progressBar.setProgress(1);
        }
        catch (IOException e)
        {
            Platform.runLater(() -> list.getItems().add(e.getMessage()));
        }
        finally
        {
            connection.disconnect();
        }
    }
}
