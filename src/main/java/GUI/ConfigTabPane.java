package main.java.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.java.BasicNetworking.Interface;
import main.java.GUI.DHCP.DHCPTab;
import main.java.GUI.EIGRP.EIGRPTab;
import main.java.GUI.ISIS.ISISTab;
import main.java.GUI.Interface.InterfacesTab;
import main.java.GUI.LoggingAndStatistics.LoggingAndStatisticsTab;
import main.java.GUI.OSPF.OSPFTab;
import main.java.GUI.Other.OtherTab;
import main.java.GUI.RIP.RIPTab;
import main.java.GUI.Redundancy.RedundancyTab;
import main.java.GUI.Services.ServicesTab;
import main.java.GUI.Static.StaticRouteTab;
import main.java.GUI.Topology.TopologyTab;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ConfigTabPane extends TabPane
{
    public TopologyTab topologyTab;
    public ServicesTab servicesTab;
    public InterfacesTab interfacesTab;
    public StaticRouteTab staticRouteTab;
    public OSPFTab ospfTab;
    public ISISTab isisTab;
    public EIGRPTab eigrpTab;
    public RIPTab ripTab;
    public DHCPTab dhcpTab;
    public OtherTab otherTab;
    public LoggingAndStatisticsTab loggingAndStatisticsTab;
    public RedundancyTab redundancyTab;

    public ConfigTabPane(Image image, String hostname, Interface @NotNull [] interfaces, Stage stage)
    {
        this.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        ObservableList<Interface> observableInterfaces = FXCollections.observableArrayList();
        observableInterfaces.addAll(Arrays.asList(interfaces));

        Tab[] tabs = new Tab[]
                {
                        topologyTab = new TopologyTab(image,stage),
                        servicesTab = new ServicesTab(hostname),
                        interfacesTab = new InterfacesTab(observableInterfaces),
                        staticRouteTab = new StaticRouteTab(observableInterfaces),
                        ospfTab = new OSPFTab(observableInterfaces),
                        isisTab = new ISISTab(observableInterfaces),
                        eigrpTab = new EIGRPTab(observableInterfaces),
                        ripTab = new RIPTab(observableInterfaces),
                        dhcpTab = new DHCPTab(observableInterfaces),
                        redundancyTab = new RedundancyTab(observableInterfaces),
                        loggingAndStatisticsTab = new LoggingAndStatisticsTab(observableInterfaces),
                        otherTab = new OtherTab(observableInterfaces)
                };

        for (Tab tab : tabs)
        {
            this.getTabs().add(tab);
        }

        this.setPadding(new Insets(10, 10, 10, 35));
    }
}
