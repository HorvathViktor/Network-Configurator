package main.java.GUI.DHCP;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.IPv4Network;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Input.EmptyOrNullInputException;
import main.java.GUI.Input.InPaneVerification;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DHCPPane
{
    public TextField poolNameTextField;
    public TextField networkTextField;
    public TextField networkMaskTextField;
    public TextField exclusionStartTextField;
    public TextField exclusionEndTextField;
    public TextField defaultGatewayTextField;
    public TextField dnsServerTextField;
    public TextField domainNameTextField;
    public TextField daysTextField;
    public TextField hoursTextField;
    public TextField minutesTextField;
    public CheckBox infiniteLeaseCheckBox;
    public ObservableList<DhcpData> data = FXCollections.observableArrayList();

    public Node constructPane()
    {
        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(10);
        box.getChildren().add(constructWestPane());
        box.getChildren().add(constructEastPane());

        return new GroupBox("DHCP", box);
    }


    @SuppressWarnings("unchecked")
    private @NotNull Node constructEastPane()
    {
        GridPane eastGrid = new GridPane();
        eastGrid.setHgap(50);
        eastGrid.setVgap(10);

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        TableView<DhcpData> dhcpTable = new TableView<>(data);
        dhcpTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DhcpData, String> poolNameColumn;
        TableColumn<DhcpData, String> networkColumn;
        TableColumn<DhcpData, String> exclusionRangeColumn;
        TableColumn<DhcpData, String> defaultGatewayColumn;
        TableColumn<DhcpData, String> dnsServerColumn;
        TableColumn<DhcpData, String> domainNameColumn;
        TableColumn<DhcpData, String> leaseColumn;

        TableColumn<String, String>[] columns = new TableColumn[]
                {
                        poolNameColumn = new TableColumn<>("Pool Name"),
                        networkColumn = new TableColumn<>("Network"),
                        exclusionRangeColumn = new TableColumn<>("Exclusion Range"),
                        defaultGatewayColumn = new TableColumn<>("Default Gateway"),
                        dnsServerColumn = new TableColumn<>("DNS Server"),
                        domainNameColumn = new TableColumn<>("Domain Name"),
                        leaseColumn = new TableColumn<>("Lease")
                };

        String[] columnData = new String[]
                {
                        "poolName",
                        "network",
                        "exclusion",
                        "gateway",
                        "dns",
                        "domain",
                        "lease",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }


        dhcpTable.getColumns().addAll(poolNameColumn, networkColumn, exclusionRangeColumn, defaultGatewayColumn, dnsServerColumn, domainNameColumn, leaseColumn);
        dhcpTable.setPrefSize(800, 400);


        eastGrid.add(add, 0, 0);
        eastGrid.add(remove, 5, 0);

        eastGrid.add(dhcpTable, 0, 1, 6, 5);


        add.setOnAction(event ->
        {

            InPaneVerification verify = new InPaneVerification();

            String poolName = verify.verifyObligatoryInput(poolNameTextField);
            String network = verify.verifyObligatoryIPv4(networkTextField);
            String networkMask = verify.verifyIPv4Mask(networkMaskTextField);
            String exclusionFirst = verify.verifyNonObligatoryIPv4(exclusionStartTextField);
            String exclusionSecond = verify.verifyNonObligatoryIPv4(exclusionEndTextField);
            if (Objects.equals(exclusionFirst, "") && !Objects.equals(exclusionSecond, ""))
            {
                verify.isFlawless = false;
                verify.makeTextFieldRed(exclusionStartTextField);
            }

            String defaultGateway = verify.verifyObligatoryIPv4(defaultGatewayTextField);
            String dnsServer = verify.verifyNonObligatoryIPv4(dnsServerTextField);
            String domain = domainNameTextField.getText();


            if (infiniteLeaseCheckBox.isSelected())
            {
                if (verify.isFlawless)
                {
                    IPv4Network poolNetwork = new IPv4Network(network, networkMask);
                    IPv4Network exclusionFirstNetwork = new IPv4Network("", "32");
                    IPv4Network exclusionSecondNetwork = new IPv4Network("", "32");
                    IPv4Network gatewayNetwork = new IPv4Network(defaultGateway, "32");
                    IPv4Network dnsNetwork = new IPv4Network("", "32");
                    if (!Objects.equals(exclusionFirst, ""))
                    {
                        exclusionFirstNetwork = new IPv4Network(exclusionFirst, "32");
                        if (!Objects.equals(exclusionSecond, ""))
                        {
                            exclusionSecondNetwork = new IPv4Network(exclusionSecond, "32");
                        }
                    }
                    if (!Objects.equals(dnsServer, ""))
                    {
                        dnsNetwork = new IPv4Network(dnsServer, "32");
                    }
                    boolean isDuplicate = false;
                    for (DhcpData row : data)
                    {
                        if (Objects.equals(row.poolName, poolName))
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "A pool with this name is already present ");
                            alert.setHeaderText("Can't add pool");
                            alert.show();
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate)
                    {
                        data.add(new DhcpData(poolName, poolNetwork, exclusionFirstNetwork, exclusionSecondNetwork, gatewayNetwork, dnsNetwork, domain, DhcpData.LeaseType.Infinite));
                    }
                }
            }
            else
            {
                boolean isDaysEmpty = false;
                int days = 0;
                try
                {
                    verify.isEmptyOrNullInput(daysTextField);
                    try
                    {
                        days = Integer.parseInt(daysTextField.getText());
                        if (days > 365 || days < 0)
                        {
                            verify.makeTextFieldRed(daysTextField);
                            verify.isFlawless = false;
                        }
                        else
                        {
                            verify.makeTextFieldNormal(daysTextField);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        verify.makeTextFieldRed(daysTextField);
                        verify.isFlawless = false;
                    }
                }
                catch (EmptyOrNullInputException e)
                {
                    isDaysEmpty = true;
                    verify.makeTextFieldNormal(daysTextField);
                }


                boolean isHoursEmpty = false;
                int hours = 0;
                try
                {
                    verify.isEmptyOrNullInput(hoursTextField);
                    try
                    {
                        hours = Integer.parseInt(hoursTextField.getText());
                        if (hours > 23 || hours < 0)
                        {
                            verify.makeTextFieldRed(hoursTextField);
                            verify.isFlawless = false;
                        }
                        else
                        {
                            verify.makeTextFieldNormal(hoursTextField);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        verify.makeTextFieldRed(hoursTextField);
                        verify.isFlawless = false;
                    }
                }
                catch (EmptyOrNullInputException e)
                {
                    isHoursEmpty = true;
                    verify.makeTextFieldNormal(hoursTextField);
                }


                boolean isMinutesEmpty = false;
                int minutes = 0;
                try
                {
                    verify.isEmptyOrNullInput(minutesTextField);
                    try
                    {
                        minutes = Integer.parseInt(minutesTextField.getText());
                        if (minutes > 59 || minutes < 0)
                        {
                            verify.makeTextFieldRed(minutesTextField);
                            verify.isFlawless = false;
                        }
                        else
                        {
                            verify.makeTextFieldNormal(minutesTextField);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        verify.makeTextFieldRed(minutesTextField);
                        verify.isFlawless = false;
                    }
                }
                catch (EmptyOrNullInputException e)
                {
                    isMinutesEmpty = true;
                    minutes = 0;
                    verify.makeTextFieldNormal(minutesTextField);
                }

                if (verify.isFlawless)
                {
                    IPv4Network poolNetwork = new IPv4Network(network, networkMask);
                    IPv4Network exclusionFirstNetwork = new IPv4Network("", "32");
                    IPv4Network exclusionSecondNetwork = new IPv4Network("", "32");
                    IPv4Network gatewayNetwork = new IPv4Network(defaultGateway, "32");
                    IPv4Network dnsNetwork = new IPv4Network("", "32");
                    if (!Objects.equals(exclusionFirst, ""))
                    {
                        exclusionFirstNetwork = new IPv4Network(exclusionFirst, "32");
                        if (!Objects.equals(exclusionSecond, ""))
                        {
                            exclusionSecondNetwork = new IPv4Network(exclusionSecond, "32");
                        }
                    }
                    if (!Objects.equals(dnsServer, ""))
                    {
                        dnsNetwork = new IPv4Network(dnsServer, "32");
                    }
                    boolean isDuplicate = false;
                    for (DhcpData row : data)
                    {
                        if (Objects.equals(row.poolName, poolName))
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "A pool with this name is already present ");
                            alert.setHeaderText("Can't add pool");
                            alert.show();
                            isDuplicate = true;
                            break;
                        }
                    }
                    if(!isDuplicate)
                    {
                        if (isDaysEmpty && isHoursEmpty && isMinutesEmpty)   //Everything is empty --> default
                        {
                            data.add(new DhcpData(poolName, poolNetwork, exclusionFirstNetwork, exclusionSecondNetwork, gatewayNetwork, dnsNetwork, domain, DhcpData.LeaseType.Default));
                        }
                        else
                        {
                            data.add(new DhcpData(poolName, poolNetwork, exclusionFirstNetwork, exclusionSecondNetwork, gatewayNetwork, dnsNetwork, domain, days, hours, minutes));
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
                data.remove(row);
            }
        });


        return eastGrid;
    }


    private @NotNull Node constructWestPane()
    {
        GridPane westGrid = new GridPane();
        westGrid.setHgap(10);
        westGrid.setVgap(30);


        Label poolName = new Label("Pool Name");
        Label network = new Label("Network");
        Label networkMask = new Label("Mask");
        Label exclusionRange = new Label("Exclusion Range");
        Label exclusionRangeSeparator = new Label("-");
        GridPane.setHalignment(exclusionRangeSeparator, HPos.CENTER);
        GridPane.setValignment(exclusionRangeSeparator, VPos.CENTER);

        Label defaultGateway = new Label("Default Gateway");
        Label dnsServer = new Label("DNS Server");
        Label domainName = new Label("Domain Name");
        Label lease = new Label("Lease");
        Label days = new Label("Days");
        Label hours = new Label("Hours");
        Label minutes = new Label("Minutes");

        poolNameTextField = new TextField();
        networkTextField = new TextField();
        networkMaskTextField = new TextField();
        exclusionStartTextField = new TextField();
        exclusionEndTextField = new TextField();
        defaultGatewayTextField = new TextField();
        dnsServerTextField = new TextField();
        domainNameTextField = new TextField();
        daysTextField = new TextField();
        daysTextField.setMaxSize(40, 25);
        hoursTextField = new TextField();
        hoursTextField.setMaxSize(40, 25);
        minutesTextField = new TextField();
        minutesTextField.setMaxSize(40, 25);

        infiniteLeaseCheckBox = new CheckBox("Infinite");

        daysTextField.editableProperty().bind(infiniteLeaseCheckBox.selectedProperty().not());
        hoursTextField.editableProperty().bind(infiniteLeaseCheckBox.selectedProperty().not());
        minutesTextField.editableProperty().bind(infiniteLeaseCheckBox.selectedProperty().not());

        daysTextField.disableProperty().bind(infiniteLeaseCheckBox.selectedProperty());
        hoursTextField.disableProperty().bind(infiniteLeaseCheckBox.selectedProperty());
        minutesTextField.disableProperty().bind(infiniteLeaseCheckBox.selectedProperty());


        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setPrefWrapLength(300);
        flowPane.getChildren().addAll(days, daysTextField, hours, hoursTextField, minutes, minutesTextField);


        westGrid.add(poolName, 0, 0);
        westGrid.add(network, 0, 1);
        westGrid.add(networkMask, 2, 1);
        westGrid.add(exclusionRange, 0, 2);
        westGrid.add(exclusionRangeSeparator, 2, 2);
        westGrid.add(defaultGateway, 0, 3);
        westGrid.add(dnsServer, 0, 4);
        westGrid.add(domainName, 0, 5);
        westGrid.add(lease, 0, 6);
        westGrid.add(flowPane, 0, 7, 2, 1);

        westGrid.add(poolNameTextField, 1, 0);
        westGrid.add(networkTextField, 1, 1);
        westGrid.add(networkMaskTextField, 3, 1);
        westGrid.add(exclusionStartTextField, 1, 2);
        westGrid.add(exclusionEndTextField, 3, 2);
        westGrid.add(defaultGatewayTextField, 1, 3);
        westGrid.add(dnsServerTextField, 1, 4);
        westGrid.add(domainNameTextField, 1, 5);

        westGrid.add(infiniteLeaseCheckBox, 1, 6);

        return westGrid;
    }


    public static class DhcpData
    {
        private String poolName;
        private IPv4Network poolNetwork;
        private String network;
        private String exclusion;
        private IPv4Network exclusionFirstNetwork;
        private IPv4Network exclusionSecondNetwork;
        private String gateway;
        private IPv4Network gatewayNetwork;
        private String dns;
        private IPv4Network dnsNetwork;
        private String domain;
        private LeaseType leaseType;

        private int days;
        private int hours;
        private int minutes;
        private String lease;

        public enum LeaseType
        {
            Infinite,Default,Custom

        }

        public DhcpData(String poolName, IPv4Network network, @NotNull IPv4Network exclusionFirstNetwork, @NotNull IPv4Network exclusionSecondNetwork,
                        @NotNull IPv4Network gatewayNetwork, @NotNull IPv4Network dnsNetwork, String domain, LeaseType lease)
        {
            this.poolName = poolName;
            this.poolNetwork = network;
            this.network = poolNetwork.getIPAddress() + poolNetwork.getPrefix();
            this.exclusionFirstNetwork = exclusionFirstNetwork;
            this.exclusionSecondNetwork = exclusionSecondNetwork;
            this.exclusion = exclusionFirstNetwork.getIPAddress() + " - " + exclusionSecondNetwork.getIPAddress();
            this.gatewayNetwork = gatewayNetwork;
            this.gateway = gatewayNetwork.getIPAddress();
            this.dnsNetwork = dnsNetwork;
            this.dns = dnsNetwork.getIPAddress();
            this.domain = domain;
            leaseType = lease;
            this.lease = lease.toString();
        }

        public DhcpData(String poolName, IPv4Network network, @NotNull IPv4Network exclusionFirstNetwork, @NotNull IPv4Network exclusionSecondNetwork,
                        @NotNull IPv4Network gatewayNetwork, @NotNull IPv4Network dnsNetwork, String domain, int days, int hours, int minutes)
        {
            this.poolName = poolName;
            this.poolNetwork = network;
            this.network = poolNetwork.getIPAddress() + poolNetwork.getPrefix();
            this.exclusionFirstNetwork = exclusionFirstNetwork;
            this.exclusionSecondNetwork = exclusionSecondNetwork;
            this.exclusion = exclusionFirstNetwork.getIPAddress() + " - " + exclusionSecondNetwork.getIPAddress();
            this.gatewayNetwork = gatewayNetwork;
            this.gateway = gatewayNetwork.getIPAddress();
            this.dnsNetwork = dnsNetwork;
            this.dns = dnsNetwork.getIPAddress();
            this.domain = domain;
            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            leaseType = LeaseType.Custom;
            lease = "Days: " + days + "\n" + "Hours: " + hours + "\n" + "Minutes: " + minutes;
        }

        public String getPoolName()
        {
            return poolName;
        }

        public void setPoolName(String poolName)
        {
            this.poolName = poolName;
        }

        public String getNetwork()
        {
            return network;
        }

        public void setNetwork(String network)
        {
            this.network = network;
        }

        public String getExclusion()
        {
            return exclusion;
        }

        public void setExclusion(String exclusion)
        {
            this.exclusion = exclusion;
        }

        public String getGateway()
        {
            return gateway;
        }

        public void setGateway(String gateway)
        {
            this.gateway = gateway;
        }

        public String getDns()
        {
            return dns;
        }

        public void setDns(String dns)
        {
            this.dns = dns;
        }

        public String getDomain()
        {
            return domain;
        }

        public void setDomain(String domain)
        {
            this.domain = domain;
        }

        public String getLease()
        {
            return lease;
        }

        public void setLease(String lease)
        {
            this.lease = lease;
        }

        public LeaseType getLeaseType()
        {
            return leaseType;
        }

        public int getDays()
        {
            return days;
        }

        public int getHours()
        {
            return hours;
        }

        public int getMinutes()
        {
            return minutes;
        }

        public IPv4Network getPoolNetwork()
        {
            return poolNetwork;
        }

        public IPv4Network getExclusionFirstNetwork()
        {
            return exclusionFirstNetwork;
        }

        public IPv4Network getExclusionSecondNetwork()
        {
            return exclusionSecondNetwork;
        }

        public IPv4Network getGatewayNetwork()
        {
            return gatewayNetwork;
        }

        public IPv4Network getDnsNetwork()
        {
            return dnsNetwork;
        }
    }


}
