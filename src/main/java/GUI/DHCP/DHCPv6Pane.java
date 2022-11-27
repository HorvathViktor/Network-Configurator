package main.java.GUI.DHCP;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Input.InPaneVerification;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DHCPv6Pane
{

    public ObservableList<DHCPv6Data> dhcpData = FXCollections.observableArrayList();
    public ObservableList<DHCPv6Pool> poolData = FXCollections.observableArrayList();

    public Node constructPane(ObservableList<Interface> interfaces)
    {
        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(10);
        box.getChildren().add(constructWestPane());
        box.getChildren().add(constructEastPane(interfaces));

        return new GroupBox("DHCPv6", box);
    }

    @SuppressWarnings("unchecked")
    private @NotNull Node constructEastPane(ObservableList<Interface> interfaces)
    {
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(35);

        Label interfacesLabel = new Label("Interfaces");
        Label poolLabel = new Label("Pool");
        Label SLAAC = new Label("SLAAC");
        Label fullDHCPv6 = new Label("Full DHCPv6");
        Label validLifetimeLabel = new Label("Valid Lifetime");
        Label preferredLifetimeLabel = new Label("Preferred Lifetime");

        ComboBox<Interface> interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();
        ComboBox<DHCPv6Pool> poolComboBox = new ComboBox<>(poolData);
        poolComboBox.getSelectionModel().selectFirst();

        CheckBox slaacChekcBox = new CheckBox();
        CheckBox fullDHCPv6CheckBox = new CheckBox();

        TextField validTimeTextField = new TextField();
        TextField preferredTimeTextField = new TextField();

        Button add = new Button("Add");
        Button remove = new Button("Remove");


        grid.add(interfacesLabel, 0, 0);
        grid.add(poolLabel, 1, 0);
        grid.add(SLAAC, 2, 0);
        grid.add(fullDHCPv6, 3, 0);
        grid.add(validLifetimeLabel, 4, 0);
        grid.add(preferredLifetimeLabel, 5, 0);

        grid.add(interfacesComboBox, 0, 1);
        grid.add(poolComboBox, 1, 1);
        grid.add(slaacChekcBox, 2, 1);
        grid.add(fullDHCPv6CheckBox, 3, 1);
        grid.add(validTimeTextField, 4, 1);
        grid.add(preferredTimeTextField, 5, 1);

        grid.add(add, 0, 2);
        grid.add(remove, 3, 2);

        TableView<DHCPv6Data> dhcpTable = new TableView<>(dhcpData);

        TableColumn<DHCPv6Data, String> interfaceColumn;
        TableColumn<DHCPv6Data, String> poolColumn;
        TableColumn<DHCPv6Data, String> slaacColumn;
        TableColumn<DHCPv6Data, String> dhcpv6Column;
        TableColumn<DHCPv6Data, String> validTimeColumn;
        TableColumn<DHCPv6Data, String> preferredTimeColumn;

        TableColumn<DHCPv6Data, String>[] columns = new TableColumn[]
                {
                        interfaceColumn = new TableColumn<>("Interface"),
                        poolColumn = new TableColumn<>("Pool"),
                        slaacColumn = new TableColumn<>("SLAAC"),
                        dhcpv6Column = new TableColumn<>("DHCPv6"),
                        validTimeColumn = new TableColumn<>("Valid Time"),
                        preferredTimeColumn = new TableColumn<>("Preferred Time")
                };

        String[] columnData = new String[]
                {
                        "ifaceName",
                        "poolName",
                        "SLAAC",
                        "DHCPV6",
                        "preferredLifetime",
                        "validLifetime"
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        dhcpTable.getColumns().addAll(interfaceColumn, poolColumn, slaacColumn, dhcpv6Column, validTimeColumn, preferredTimeColumn);
        dhcpTable.setPrefSize(400, 300);
        dhcpTable.autosize();

        grid.add(dhcpTable, 0, 3, 6, 3);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();

            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            DHCPv6Pool pool = null;
            if (poolComboBox.getItems().size() == 0 || poolComboBox.getItems() == null)
            {
                verify.isFlawless = false;
            }
            else
            {
                pool = poolComboBox.getSelectionModel().getSelectedItem();
            }
            boolean slaac = slaacChekcBox.isSelected();
            boolean dhcpv6 = fullDHCPv6CheckBox.isSelected();

            int validLifetime = verify.verifyNonObligatoryNumberInput(validTimeTextField);
            int preferredLifetime = verify.verifyNonObligatoryNumberInput(preferredTimeTextField);

            if (verify.isFlawless)
            {
                if (!slaac && !dhcpv6)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The interface must either be configured as SLAAC, DHCPv6 or both but never neither");
                    alert.setHeaderText("Pool can't be assigned");
                    alert.show();
                }
                else
                {
                    if (dhcpv6 && pool.poolAddress.getIPAddress().equals(""))
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "This pool does not support full DHCPv6 (no prefix delegation)");
                        alert.setHeaderText("Pool can't be assigned");
                        alert.show();
                    }
                    else
                    {
                        boolean isDuplicate = false;
                        for (DHCPv6Data row : dhcpData)
                        {
                            if (Objects.equals(row.iface, iface))
                            {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Can't assign pool for that interface because it already has one assigned ");
                                alert.setHeaderText("Pool can't be assigned");
                                alert.show();
                                isDuplicate = true;
                                break;
                            }
                        }
                        if (!isDuplicate)
                        {
                            dhcpData.add(new DHCPv6Data(iface, pool, slaac, dhcpv6, validLifetime, preferredLifetime));
                        }
                    }
                }
            }
        });

        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = dhcpTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                dhcpData.remove(row);
            }
        });

        return grid;
    }

    @SuppressWarnings({"unchecked"})
    private @NotNull Node constructWestPane()
    {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(30);

        Label poolNameLabel = new Label("Pool Name");
        Label networkLabel = new Label("Network");
        Label prefixLabel = new Label("/");
        Label dnsServerLabel = new Label("DNS Server");
        Label domainNameLabel = new Label("Domain Name");

        TextField poolNameTextField = new TextField();
        TextField networkTextField = new TextField();
        TextField prefixTextField = new TextField();
        prefixTextField.setMaxSize(40, 25);
        TextField dnsServerTextField = new TextField();
        TextField domainNameTextField = new TextField();

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        TableView<DHCPv6Pool> poolTable = new TableView<>(poolData);

        TableColumn<DHCPv6Pool, String> poolNameColumn;
        TableColumn<DHCPv6Pool, String> networkColumn;
        TableColumn<DHCPv6Pool, String> dnsServerColumn;
        TableColumn<DHCPv6Pool, String> domainNameColumn;

        TableColumn<DHCPv6Pool, String>[] columns = new TableColumn[]
                {
                        poolNameColumn = new TableColumn<>("Pool Name"),
                        networkColumn = new TableColumn<>("Network"),
                        dnsServerColumn = new TableColumn<>("DNS Server"),
                        domainNameColumn = new TableColumn<>("Domain Name")
                };

        String[] columnData = new String[]
                {
                        "poolName",
                        "ip",
                        "dns",
                        "domainName",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        poolTable.getColumns().addAll(poolNameColumn, networkColumn, dnsServerColumn, domainNameColumn);
        poolTable.setPrefSize(400, 300);
        poolTable.autosize();

        grid.add(poolNameLabel, 0, 0);
        grid.add(networkLabel, 0, 1);
        grid.add(prefixLabel, 2, 1);
        grid.add(dnsServerLabel, 0, 2);
        grid.add(domainNameLabel, 0, 3);

        grid.add(poolNameTextField, 1, 0);
        grid.add(networkTextField, 1, 1);
        grid.add(prefixTextField, 3, 1);
        grid.add(dnsServerTextField, 1, 2);
        grid.add(domainNameTextField, 1, 3);

        grid.add(add, 0, 4);
        grid.add(remove, 4, 4);

        grid.add(poolTable, 0, 5, 5, 1);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();
            String poolName = verify.verifyObligatoryInput(poolNameTextField);

            String network = verify.verifyNonObligatoryIPv6(networkTextField);
            String prefix = null;
            if (!Objects.equals(network, ""))
            {
                prefix = verify.verifyIPv6Prefix(prefixTextField);
            }
            else
            {
                verify.makeTextFieldNormal(prefixTextField);
            }
            String dnsServer = verify.verifyObligatoryIPv6(dnsServerTextField);
            String domainName = domainNameTextField.getText();

            if (verify.isFlawless)
            {
                boolean isDuplicate = false;
                for (DHCPv6Pool row : poolData)
                {
                    if (Objects.equals(row.poolName, poolName))
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The pool can't be created because there is already one with this name!");
                        alert.setHeaderText("Pool can't be created");
                        alert.show();
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    poolData.add(new DHCPv6Pool(poolName, new IPv6Network(network, prefix), new IPv6Network(dnsServer, "128"), domainName));
                }
            }

        });

        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = poolTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                poolData.remove(row);
            }
        });

        return grid;
    }


    public static class DHCPv6Data
    {
        private Interface iface;
        private String ifaceName;
        private DHCPv6Pool pool;
        private String poolName;
        private boolean isSLAACEnabled;
        private String SLAAC;
        private boolean isDHCPv6Enabled;
        private String DHCPV6;
        private String preferredLifetimeForTable;
        private int preferredLifetime;
        private String validLifetimeForTable;
        private int validLifetime;

        public DHCPv6Data(Interface iface, DHCPv6Pool pool, boolean isSLAACEnabled, boolean isDHCPv6Enabled, int preferredLifetime, int validLifetime)
        {
            this.iface = iface;
            this.ifaceName = iface.getInterface();
            this.pool = pool;
            this.poolName = pool.poolName;
            this.isSLAACEnabled = isSLAACEnabled;
            this.SLAAC = isSLAACEnabled ? "Enabled" : "Disabled";
            this.isDHCPv6Enabled = isDHCPv6Enabled;
            this.DHCPV6 = isDHCPv6Enabled ? "Enabled" : "Disabled";

            if (preferredLifetime == -1 || preferredLifetime == 0)
            {
                this.preferredLifetimeForTable = "Default";
                this.preferredLifetime = -1;
            }
            else
            {
                this.preferredLifetimeForTable = String.valueOf(preferredLifetime);
                this.preferredLifetime = preferredLifetime;
            }
            if (validLifetime == -1 || validLifetime == 0)
            {
                this.validLifetimeForTable = "Default";
                this.validLifetime = -1;
            }
            else
            {
                this.validLifetimeForTable = String.valueOf(validLifetime);
                this.validLifetime = validLifetime;
            }

        }

        public Interface getIface()
        {
            return iface;
        }

        public String getIfaceName()
        {
            return ifaceName;
        }

        public DHCPv6Pool getPool()
        {
            return pool;
        }

        public String getPoolName()
        {
            return poolName;
        }

        public boolean isSLAACEnabled()
        {
            return isSLAACEnabled;
        }

        public String getSLAAC()
        {
            return SLAAC;
        }

        public boolean isDHCPv6Enabled()
        {
            return isDHCPv6Enabled;
        }

        public String getDHCPV6()
        {
            return DHCPV6;
        }

        public String getPreferredLifetimeForTable()
        {
            return preferredLifetimeForTable;
        }

        public String getValidLifetimeForTable()
        {
            return validLifetimeForTable;
        }

        public int getPreferredLifetime()
        {
            return preferredLifetime;
        }

        public int getValidLifetime()
        {
            return validLifetime;
        }
    }

    public static class DHCPv6Pool
    {
        private String poolName;
        private IPv6Network poolAddress;
        private String ip;
        private IPv6Network dnsServer;
        private String dns;
        private String domainName;
        private boolean isFullDHCP;


        public DHCPv6Pool(String poolName, IPv6Network poolAddress, IPv6Network dnsServer, String domainName)
        {
            this.poolName = poolName;
            this.poolAddress = poolAddress;
            this.ip = !Objects.equals(poolAddress.getIPAddress(), "") ? poolAddress.getIPAddress() + poolAddress.getPrefix() : "Not specified";
            this.isFullDHCP = !Objects.equals(poolAddress.getIPAddress(), "");
            this.dnsServer = dnsServer;
            this.dns = dnsServer.getIPAddress();
            this.domainName = !Objects.equals(domainName, "") ? domainName : "";
        }

        @Override
        public String toString()
        {
            return poolName;
        }

        public String getPoolName()
        {
            return poolName;
        }

        public IPv6Network getPoolAddress()
        {
            return poolAddress;
        }

        public String getIp()
        {
            return ip;
        }

        public IPv6Network getDnsServer()
        {
            return dnsServer;
        }

        public String getDns()
        {
            return dns;
        }

        public String getDomainName()
        {
            return domainName;
        }

        public boolean isFullDHCP()
        {
            return isFullDHCP;
        }
    }
}
