package main.java.GUI.DHCP;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Input.InPaneVerification;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DHCPRelayPane
{
    public ObservableList<DHCPRelayData> data = FXCollections.observableArrayList();

    @SuppressWarnings("unchecked")
    public Node constructDHCPRelayPane(ObservableList<Interface> interfaces)
    {
        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(20));
        mainGrid.setVgap(15);
        mainGrid.setHgap(10);

        Label interfaceLabel = new Label("Interface");
        Label helperLabel = new Label("IPv4 DHCP Helper");
        Label relayLabel = new Label("IPv6 DHCP Relay");

        ComboBox<Interface> interfaceComboBox = new ComboBox<>(interfaces);
        interfaceComboBox.getSelectionModel().selectFirst();

        TextField ipv4HelperTextField = new TextField();
        TextField ipv6RelayTextField = new TextField();

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        TableView<DHCPRelayData> dhcpRelayTable = new TableView<>(data);
        dhcpRelayTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DHCPRelayData, String> interfacesColumn;
        TableColumn<DHCPRelayData, String> dhcpV4HelperColumn;
        TableColumn<DHCPRelayData, String> dhcpV6RelayColumn;

        TableColumn<String, String>[] columns = new TableColumn[]
                {
                        interfacesColumn = new TableColumn<>("Interface"),
                        dhcpV4HelperColumn = new TableColumn<>("DHCP Server IPv4"),
                        dhcpV6RelayColumn = new TableColumn<>("DHCP Server IPv6"),
                };

        String[] columnData = new String[]
                {
                        "ifaceName",
                        "ip",
                        "ipv6",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        dhcpRelayTable.getColumns().addAll(interfacesColumn, dhcpV4HelperColumn, dhcpV6RelayColumn);
        dhcpRelayTable.setPrefSize(800, 400);

        mainGrid.add(interfaceLabel, 0, 0);
        mainGrid.add(helperLabel, 0, 1);
        mainGrid.add(relayLabel, 0, 2);

        mainGrid.add(interfaceComboBox, 1, 0);
        mainGrid.add(ipv4HelperTextField, 1, 1);
        mainGrid.add(ipv6RelayTextField, 1, 2);

        mainGrid.add(add, 0, 3);
        mainGrid.add(remove, 2, 3);

        mainGrid.add(dhcpRelayTable, 0, 4, 3, 2);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();

            String ipv4Helper = verify.verifyNonObligatoryIPv4(ipv4HelperTextField);
            String ipv6Relay = verify.verifyNonObligatoryIPv6(ipv6RelayTextField);

            if (Objects.equals(ipv4Helper, "") && Objects.equals(ipv6Relay, ""))
            {
                verify.isFlawless = false;
                Alert alert = new Alert(Alert.AlertType.ERROR, "Requires IPv4 or Ipv6 address");
                alert.setHeaderText("Can't add DHCP Helper or Relay");
                alert.show();
            }

            if (verify.isFlawless)
            {
                IPv4Network ipv4HelperNetwork = new IPv4Network("", "30");
                IPv6Network ipv6RelayNetwork = new IPv6Network("", "64");
                Interface iface = interfaceComboBox.getSelectionModel().getSelectedItem();
                if (!Objects.equals(ipv4Helper, ""))
                {
                    ipv4HelperNetwork = new IPv4Network(ipv4Helper, "32");
                }
                if (!Objects.equals(ipv6Relay, ""))
                {
                    ipv6RelayNetwork = new IPv6Network(ipv6Relay, "64");
                }
                if (verify.isFlawless)
                {
                    boolean isDuplicate = false;
                    for (DHCPRelayData row : data)
                    {
                        if (row.iface == iface && (row.ipV4Network.getIPAddress().equals(ipv4HelperNetwork.getIPAddress()) || Objects.equals(row.ipV6Network.getIPAddress(), ipv6RelayNetwork.getIPAddress())))
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "The relay can't be added because this interface with this address already exists");
                            alert.setHeaderText("Can't add helper/relay");
                            alert.show();
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate)
                    {
                        data.add(new DHCPRelayData(iface, ipv4HelperNetwork, ipv6RelayNetwork));
                    }
                }
            }
        });
        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = dhcpRelayTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                data.remove(row);
            }
        });

        return new GroupBox("DHCP Helper/Relay", mainGrid);
    }

    public static class DHCPRelayData
    {
        private Interface iface;
        private String ifaceName;
        private IPv4Network ipV4Network;
        private String ip;
        private IPv6Network ipV6Network;
        private String ipv6;

        public DHCPRelayData(@NotNull Interface iface, @NotNull IPv4Network ipV4Network, @NotNull IPv6Network ipV6Network)
        {
            this.iface = iface;
            ifaceName = iface.getInterface();
            this.ipV4Network = ipV4Network;
            ip = !Objects.equals(ipV4Network.getIPAddress(), "") ? ipV4Network.getIPAddress() : "No";
            this.ipV6Network = ipV6Network;
            ipv6 = !Objects.equals(ipV6Network.getIPAddress(), "") ? ipV6Network.getIPAddress() : "No";
        }

        public Interface getIface()
        {
            return iface;
        }

        public String getIfaceName()
        {
            return ifaceName;
        }

        public IPv4Network getIpV4Network()
        {
            return ipV4Network;
        }

        public String getIp()
        {
            return ip;
        }

        public IPv6Network getIpV6Network()
        {
            return ipV6Network;
        }

        public String getIpv6()
        {
            return ipv6;
        }
    }
}
