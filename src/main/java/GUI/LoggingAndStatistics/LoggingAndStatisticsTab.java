package main.java.GUI.LoggingAndStatistics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import main.java.BasicNetworking.*;
import main.java.Configs.Protocols.Utility.SyslogConfig;
import main.java.GUI.BasicTab;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Component.OnOffSwitch;
import main.java.GUI.Input.InPaneVerification;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LoggingAndStatisticsTab extends BasicTab
{

    public OnOffSwitch syslogOnOffSwitch;
    public ComboBox<Interface> interfacesComboBoxSyslog;
    public ComboBox<SyslogConfig.LoggingLevel> trapLevelComboBox;
    public ComboBox<SyslogConfig.LoggingLevel> monitorLevelComboBox;
    public ObservableList<IPNetwork> syslogServersData = FXCollections.observableArrayList();
    public CheckBox specifySourceInterfaceSyslogCheckBox;

    public OnOffSwitch netflowOnOffSwitch;
    public CheckBox version9CheckBox;
    public ComboBox<Interface> interfacesComboBoxNetflow;
    public CheckBox specifySourceInterfaceNetflowCheckBox;
    public ObservableList<NetflowServer> netflowNetworks = FXCollections.observableArrayList();
    public ObservableList<NetflowData> netflowData = FXCollections.observableArrayList();


    public LoggingAndStatisticsTab(ObservableList<Interface> interfaces)
    {
        super("Logging & Statistics");
        VBox box = new VBox();
        box.setSpacing(20);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        box.getChildren().add(constructSyslogPane(interfaces));
        box.getChildren().add(constructNetflowPane(interfaces));

        anchorPane.getChildren().add(box);
    }

    @Contract("_ -> new")
    private @NotNull Node constructSyslogPane(ObservableList<Interface> interfaces)
    {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(25);
        gridPane.setHgap(35);
        gridPane.setPadding(new Insets(20));

        Label enableLogging = new Label("Enable logging to syslog server");
        Label trapLevel = new Label("Set Trap level");
        Label monitorLevel = new Label("Set Monitor level");
        Label syslogServerIP = new Label("Syslog Server IP");

        syslogOnOffSwitch = new OnOffSwitch();

        specifySourceInterfaceSyslogCheckBox = new CheckBox("Specify source interface");

        interfacesComboBoxSyslog = new ComboBox<>(interfaces);
        interfacesComboBoxSyslog.getSelectionModel().selectFirst();
        SyslogConfig.LoggingLevel[] syslogLevels = new SyslogConfig.LoggingLevel[]
                {
                        SyslogConfig.LoggingLevel.EMERGENCY,
                        SyslogConfig.LoggingLevel.ALERTS,
                        SyslogConfig.LoggingLevel.CRITICAL,
                        SyslogConfig.LoggingLevel.ERRORS,
                        SyslogConfig.LoggingLevel.WARNINGS,
                        SyslogConfig.LoggingLevel.NOTIFICATIONS,
                        SyslogConfig.LoggingLevel.INFORMATIONAL,
                        SyslogConfig.LoggingLevel.DEBUGGING
                };
        trapLevelComboBox = new ComboBox<>();
        trapLevelComboBox.getItems().addAll(syslogLevels);
        trapLevelComboBox.getSelectionModel().select(SyslogConfig.LoggingLevel.WARNINGS);
        monitorLevelComboBox = new ComboBox<>();
        monitorLevelComboBox.getItems().addAll(syslogLevels);
        monitorLevelComboBox.getSelectionModel().select(SyslogConfig.LoggingLevel.WARNINGS);


        TextField syslogIPTextField = new TextField();

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        ListView<String> syslogServerList = new ListView<>();
        syslogServerList.setPrefSize(200, 200);

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(syslogOnOffSwitch);

        gridPane.add(enableLogging, 0, 0);
        gridPane.add(trapLevel, 0, 2);
        gridPane.add(monitorLevel, 0, 3);
        gridPane.add(syslogServerIP, 0, 4);

        gridPane.add(flowPane, 1, 0);


        gridPane.add(specifySourceInterfaceSyslogCheckBox, 0, 1);

        gridPane.add(interfacesComboBoxSyslog, 1, 1);
        gridPane.add(trapLevelComboBox, 1, 2);
        gridPane.add(monitorLevelComboBox, 1, 3);

        gridPane.add(syslogIPTextField, 1, 4);


        gridPane.add(add, 0, 5);
        gridPane.add(remove, 1, 5);

        gridPane.add(syslogServerList, 0, 6);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();

            String syslogIP = verify.verifyObligatoryIP(syslogIPTextField);
            if (verify.isFlawless)
            {
                IPNetwork network;
                try
                {
                    IPAddressValidator.isValidIPv4(syslogIP);
                    network = new IPv4Network(syslogIP, "32");

                }
                catch (InvalidIPException e)
                {
                    network = new IPv6Network(syslogIP,"128");
                }
                syslogServerList.getItems().add(network.getIPAddress());
                syslogServersData.add(network);
            }
        });

        remove.setOnAction(event ->
        {
            int index = syslogServerList.getSelectionModel().getSelectedIndex();
            if (index != -1)
            {
                syslogServerList.getItems().remove(index);
                syslogServersData.remove(index);
            }
        });

        return new GroupBox("Syslog", gridPane);
    }

    @SuppressWarnings("unchecked")
    @Contract("_ -> new")
    private @NotNull Node constructNetflowPane(ObservableList<Interface> interfaces)
    {

        HBox box = new HBox();
        box.setSpacing(10);

        GridPane westGrid = new GridPane();
        westGrid.setVgap(25);
        westGrid.setHgap(35);
        westGrid.setPadding(new Insets(20));

        Label enabled = new Label("Enable Netflow process");
        Label version9 = new Label("Version 9");
        Label netflowServerIp = new Label("Netflow Server IP");
        Label port = new Label("Port");

        netflowOnOffSwitch = new OnOffSwitch();
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(netflowOnOffSwitch);

        version9CheckBox = new CheckBox();
        specifySourceInterfaceNetflowCheckBox = new CheckBox("Specify source interface");

        interfacesComboBoxNetflow = new ComboBox<>(interfaces);
        interfacesComboBoxNetflow.getSelectionModel().selectFirst();

        TextField netflowServerIpTextField = new TextField();
        TextField netflowServerPortTextField = new TextField();

        Button addNetflowServer = new Button("Add");
        Button removeNetflowServer = new Button("Remove");

        ListView<String> netflowServerList = new ListView<>();
        netflowServerList.setPrefSize(200, 200);

        westGrid.add(enabled, 0, 0);
        westGrid.add(version9, 0, 1);
        westGrid.add(specifySourceInterfaceNetflowCheckBox, 0, 2);
        westGrid.add(netflowServerIp, 0, 3);
        westGrid.add(port, 0, 4);

        westGrid.add(flowPane, 1, 0);
        westGrid.add(version9CheckBox, 1, 1);
        westGrid.add(interfacesComboBoxNetflow, 1, 2);
        westGrid.add(netflowServerIpTextField, 1, 3);
        westGrid.add(netflowServerPortTextField, 1, 4);


        westGrid.add(addNetflowServer, 0, 6);
        westGrid.add(removeNetflowServer, 1, 6);

        westGrid.add(netflowServerList, 0, 7);

        GridPane eastGrid = new GridPane();
        eastGrid.setHgap(100);
        eastGrid.setVgap(35);
        eastGrid.setPadding(new Insets(20));

        Label interfaceLabel = new Label("Interface");
        Label inbound = new Label("Inbound");
        Label outbound = new Label("Outbound");

        ComboBox<Interface> interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();

        CheckBox inBoundCheckBox = new CheckBox();
        CheckBox outBoundCheckBox = new CheckBox();

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        eastGrid.add(interfaceLabel, 0, 0);
        eastGrid.add(inbound, 1, 0);
        eastGrid.add(outbound, 2, 0);

        eastGrid.add(interfacesComboBox, 0, 1);
        eastGrid.add(inBoundCheckBox, 1, 1);
        eastGrid.add(outBoundCheckBox, 2, 1);

        eastGrid.add(add, 0, 2);
        eastGrid.add(remove, 2, 2);


        TableView<NetflowData> netflowTable = new TableView<>(netflowData);

        netflowTable.setPrefSize(500, 250);
        netflowTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<NetflowData, String> interfaceColumn;
        TableColumn<NetflowData, String> inboundColumn;
        TableColumn<NetflowData, String> outboundColumn;

        TableColumn<NetflowData, String>[] columns = new TableColumn[]
                {
                        interfaceColumn = new TableColumn<>("Interface"),
                        inboundColumn = new TableColumn<>("Inbound"),
                        outboundColumn = new TableColumn<>("Outbound"),
                };

        String[] columnData = new String[]
                {
                        "iface",
                        "in",
                        "out"
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        netflowTable.getColumns().addAll(interfaceColumn, inboundColumn, outboundColumn);

        eastGrid.add(netflowTable, 0, 3, 4, 4);

        box.getChildren().add(westGrid);
        box.getChildren().add(eastGrid);

        addNetflowServer.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();

            String netflowIP = verify.verifyObligatoryIPv4(netflowServerIpTextField);
            int netflowPort = verify.verifyObligatoryNumberInput(netflowServerPortTextField);

            if (netflowPort < 0 || netflowPort > 65535)
            {
                verify.isFlawless = false;
            }
            if (verify.isFlawless)
            {
                IPv4Network net = new IPv4Network(netflowIP, "32");
                netflowNetworks.add(new NetflowServer(net, netflowPort));
                netflowServerList.getItems().add(netflowIP + ":" + netflowPort);
            }
        });

        removeNetflowServer.setOnAction(event ->
        {
            int index = netflowServerList.getSelectionModel().getSelectedIndex();
            if (index != -1)
            {
                netflowNetworks.remove(index);
                netflowServerList.getItems().remove(index);
            }
        });

        add.setOnAction(event ->
        {
            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            boolean in = inBoundCheckBox.isSelected();
            boolean out = outBoundCheckBox.isSelected();

            if (!in && !out)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Inbound or outbound should be set");
                alert.setHeaderText("Can't add Netflow interface");
                alert.show();
            }
            else
            {
                boolean isDuplicate = false;
                for (NetflowData element : netflowData)
                {
                    if (element.iface.equals(iface))
                    {
                        isDuplicate = true;
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The interface can't be added because it has already been added.");
                        alert.setHeaderText("Can't add Netflow interface");
                        alert.show();
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    netflowData.add(new NetflowData(iface, in, out));
                }
            }
        });

        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = netflowTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                netflowData.remove(row);
            }
        });

        return new GroupBox("Netflow", box);
    }

    public record NetflowServer(IPv4Network server, int port)
    {
    }

    public static class NetflowData
    {
        private Interface iface;
        private boolean isInboundEnabled;
        private String in;
        private boolean isOutBoundEnabled;
        private String out;

        public NetflowData(Interface iface, boolean isInboundEnabled, boolean isOutBoundEnabled)
        {
            this.iface = iface;
            this.isInboundEnabled = isInboundEnabled;
            in = isInboundEnabled ? "Enabled" : "Disabled";
            this.isOutBoundEnabled = isOutBoundEnabled;
            out = isOutBoundEnabled ? "Enabled" : "Disabled";
        }

        public Interface getIface()
        {
            return iface;
        }

        public boolean isInboundEnabled()
        {
            return isInboundEnabled;
        }

        public String getIn()
        {
            return in;
        }

        public boolean isOutBoundEnabled()
        {
            return isOutBoundEnabled;
        }

        public String getOut()
        {
            return out;
        }
    }
}
