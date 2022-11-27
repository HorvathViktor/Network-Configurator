package main.java.GUI;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.java.BasicNetworking.EthernetInterface;
import main.java.BasicNetworking.EthernetTypes;
import main.java.BasicNetworking.Interface;
import main.java.BasicNetworking.Router;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class MainWindow extends Application
{
    @Override
    public void start(@NotNull Stage stage)
    {

        stage.setTitle("Network configurator");
        stage.setMaximized(true);
        TabPane mainTabPane = new TabPane();
        mainTabPane.setSide(Side.LEFT);
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Interface[] interfaces = new Interface[]
                {
                        new EthernetInterface(EthernetTypes.FastEthernet, 0, 0),
                        new EthernetInterface(EthernetTypes.FastEthernet, 1, 0),
                        new EthernetInterface(EthernetTypes.GigabitEthernet, 2, 0),
                        new EthernetInterface(EthernetTypes.GigabitEthernet, 3, 0),
                        new EthernetInterface(EthernetTypes.GigabitEthernet, 4, 0),
                        new EthernetInterface(EthernetTypes.GigabitEthernet, 5, 0),
                        new EthernetInterface(EthernetTypes.GigabitEthernet, 6, 0),
                };

        Router[] routers = new Router[]
                {
                        new Router(interfaces, "R1", "localhost", 5000),
                        new Router(interfaces, "R2", "localhost", 5001),
                        new Router(interfaces, "R3", "localhost", 5002),
                        new Router(interfaces, "R4", "localhost", 5003),
                        new Router(interfaces, "R5", "localhost", 5004),
                        new Router(interfaces, "R6", "localhost", 5005),

                };
        Image[] images = new Image[]
                {
                        createImage(this, "/images/R1.png"),
                        createImage(this, "/images/R2.png"),
                        createImage(this, "/images/R3.png"),
                        createImage(this, "/images/R4.png"),
                        createImage(this, "/images/R5.png"),
                        createImage(this, "/images/R6.png"),
                };

        Tab[] tabs = new Tab[routers.length + 1];

        for (int i = 0; i < routers.length; i++)
        {
            tabs[i] = new Tab(routers[i].hostname());
            tabs[i].setContent(new ConfigTabPane(images[i], tabs[i].getText(), routers[i].interfaces(),stage));
        }
        tabs[tabs.length - 1] = new StartConfigTab(routers, tabs);

        mainTabPane.getTabs().addAll(tabs);

        Scene scene = new Scene(mainTabPane);
        stage.setScene(scene);
        stage.show();

    }

    private static @NotNull Image createImage(@NotNull Object context, String resourceName)
    {
        URL url = context.getClass().getResource(resourceName);
        return new Image(url.toExternalForm());
    }
}
