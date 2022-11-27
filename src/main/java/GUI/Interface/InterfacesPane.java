package main.java.GUI.Interface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.IPv4Network;
import main.java.BasicNetworking.IPv6Network;
import main.java.BasicNetworking.Interface;
import main.java.BasicNetworking.Subinterface;
import main.java.Configs.Protocols.Interface.SubinterfaceConfig;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Component.OnOffSwitch;
import main.java.GUI.Input.EmptyOrNullInputException;
import main.java.GUI.Input.InPaneVerification;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InterfacesPane
{

    public OnOffSwitch adminStatusSwitch;
    public TextField ipv4AddressTextField;
    public TextField ipv4MaskTextField;
    public TextField ipv6AddressTextField;
    public TextField ipv6PrefixTextField;
    public TextField ipv6LinkLocalTextField;
    public CheckBox autoLinkLocalCheckBox;
    public ObservableList<SubinterfaceData> data = FXCollections.observableArrayList();
    public ObservableList<Interface> interfaces;


    public InterfacesPane(ObservableList<Interface> interfaces)
    {
        this.interfaces = interfaces;
    }

    public Node constructPane(Interface iface)
    {
        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(20));
        mainGrid.setVgap(15);
        mainGrid.setHgap(10);

        Label adminStatus = new Label("Admin Status");
        adminStatusSwitch = new OnOffSwitch();

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.getChildren().add(adminStatus);
        flowPane.getChildren().add(adminStatusSwitch);
        mainGrid.add(flowPane, 0, 0);

        mainGrid.add(constructIPv4Pane(), 0, 1);
        mainGrid.add(constructIPv6Pane(), 1, 1);
        mainGrid.add(constructSubinterfacesPane(iface), 0, 2, 2, 1);

        GroupBox mainGroup = new GroupBox(iface.getType() + iface.getInterfaceID(), mainGrid);

        AnchorPane.setTopAnchor(mainGroup, 0.0);
        AnchorPane.setRightAnchor(mainGroup, 0.0);
        AnchorPane.setBottomAnchor(mainGroup, 0.0);
        AnchorPane.setLeftAnchor(mainGroup, 0.0);


        return mainGroup;


    }


    @SuppressWarnings("unchecked")
    private @NotNull Node constructSubinterfacesPane(Interface iface)
    {

        HBox box = new HBox();
        GridPane westGrid = new GridPane();
        GridPane eastGrid = new GridPane();

        westGrid.setVgap(10);
        westGrid.setHgap(15);

        eastGrid.setVgap(10);
        eastGrid.setHgap(15);

        Label vlan = new Label("VLAN");
        Label encapsulationLabel = new Label("Encapsulation");
        Label ipv4AddressLabel = new Label("IPv4 Address");
        Label ipv4Mask = new Label("Mask/Prefix");
        Label ipv6Address = new Label("IPv6 Address");
        Label ipv6Prefix = new Label("/");
        Label ipv6LinkLocal = new Label("Link-Local");
        Label ipv6Auto = new Label("Autogenerate Link-Local address");

        TextField subinterfaceVLANTextField = new TextField();
        TextField subinterfaceIPv4AddressTextField = new TextField();
        TextField subinterfaceIPv4MaskTextField = new TextField();
        TextField subinterfaceIPv6AddressTextField = new TextField();
        TextField subinterfaceIPv6PrefixTextField = new TextField();
        subinterfaceIPv6PrefixTextField.setPrefSize(35, 15);
        TextField subinterfaceIPv6LinkLocalTextField = new TextField();


        final String[] encapsulationOptions = new String[]{"dot1Q", "isl"};
        ComboBox<String> subinterfaceEncapsulationComboBox = new ComboBox<>();
        subinterfaceEncapsulationComboBox.getItems().addAll(encapsulationOptions);
        subinterfaceEncapsulationComboBox.getSelectionModel().selectFirst();

        CheckBox subinterfaceAutoLinkLocalCheckBox = new CheckBox();

        subinterfaceIPv6LinkLocalTextField.editableProperty().bind(subinterfaceAutoLinkLocalCheckBox.selectedProperty().not());
        subinterfaceIPv6LinkLocalTextField.disableProperty().bind(subinterfaceAutoLinkLocalCheckBox.selectedProperty());

        Button addSubinterface = new Button("Add");
        Button removeSubinterface = new Button("Remove");


        TableView<SubinterfaceData> subinterfacesTable = new TableView<>();
        subinterfacesTable.setPrefSize(500, 250);
        subinterfacesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SubinterfaceData, String> vlanColumn;
        TableColumn<SubinterfaceData, String> encapsulationColumn;
        TableColumn<SubinterfaceData, String> ipv4Column;
        TableColumn<SubinterfaceData, String> ipv6Column;
        TableColumn<SubinterfaceData, String> linkLocalColumn;

        TableColumn<SubinterfaceData, String>[] columns = new TableColumn[]
                {
                        vlanColumn = new TableColumn<>("VLAN"),
                        encapsulationColumn = new TableColumn<>("Encapsulation"),
                        ipv4Column = new TableColumn<>("IPv4 Address"),
                        ipv6Column = new TableColumn<>("IPv6 Address"),
                        linkLocalColumn = new TableColumn<>("Link-Local"),
                };

        String[] columnData = new String[]
                {
                        "vlan",
                        "encapsulation",
                        "ipv4Network",
                        "ipv6Network",
                        "linkLocal",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        subinterfacesTable.getColumns().addAll(vlanColumn, encapsulationColumn, ipv4Column, ipv6Column, linkLocalColumn);

        westGrid.add(vlan, 0, 0);
        westGrid.add(encapsulationLabel, 0, 1);


        westGrid.add(subinterfaceVLANTextField, 1, 0);

        westGrid.add(subinterfaceEncapsulationComboBox, 1, 1);


        eastGrid.add(subinterfacesTable, 4, 0, 1, 6);

        eastGrid.add(ipv4AddressLabel, 0, 0);
        eastGrid.add(ipv4Mask, 0, 1);
        eastGrid.add(ipv6Address, 0, 2);
        eastGrid.add(ipv6Prefix, 2, 2);
        eastGrid.add(ipv6LinkLocal, 0, 3);
        eastGrid.add(ipv6Auto, 0, 4);

        eastGrid.add(subinterfaceIPv4AddressTextField, 1, 0);
        eastGrid.add(subinterfaceIPv4MaskTextField, 1, 1);
        eastGrid.add(subinterfaceIPv6AddressTextField, 1, 2);
        eastGrid.add(subinterfaceIPv6PrefixTextField, 3, 2);
        eastGrid.add(subinterfaceIPv6LinkLocalTextField, 1, 3);

        eastGrid.add(subinterfaceAutoLinkLocalCheckBox, 1, 4);

        eastGrid.add(addSubinterface, 0, 5);
        eastGrid.add(removeSubinterface, 1, 5);

        eastGrid.setPadding(new Insets(15));
        westGrid.setPadding(new Insets(15));

        box.getChildren().add(westGrid);
        box.getChildren().add(eastGrid);

        addSubinterface.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();

            String subinterfaceIpv4Address = verify.verifyNonObligatoryIPv4(subinterfaceIPv4AddressTextField);
            String subinterfaceIpv4Mask = null;
            if (!Objects.equals(subinterfaceIpv4Address, ""))
            {
                subinterfaceIpv4Mask = verify.verifyIPv4Mask(subinterfaceIPv4MaskTextField);
            }
            String subinterfaceIpv6Address = verify.verifyNonObligatoryIPv6(subinterfaceIPv6AddressTextField);
            String subinterfaceIpv6Prefix = null;
            if (!Objects.equals(subinterfaceIpv6Address, ""))
            {
                subinterfaceIpv6Prefix = verify.verifyIPv6Prefix(subinterfaceIPv6PrefixTextField);
            }

            int subinterfaceVlan = -1;
            try
            {
                verify.isEmptyOrNullInput(subinterfaceVLANTextField);
                subinterfaceVlan = Integer.parseInt(subinterfaceVLANTextField.getText());
                verify.makeTextFieldNormal(subinterfaceVLANTextField);
            }
            catch (EmptyOrNullInputException | NumberFormatException e)
            {
                verify.isFlawless = false;
                verify.makeTextFieldRed(subinterfaceVLANTextField);
            }
            String subinterfaceEncapsulation = subinterfaceEncapsulationComboBox.getSelectionModel().getSelectedItem();
            SubinterfaceConfig.Encapsulation encapsulation;
            if (subinterfaceEncapsulation.equals(SubinterfaceConfig.Encapsulation.dot1Q.toString()))
            {
                encapsulation = SubinterfaceConfig.Encapsulation.dot1Q;
            }
            else
            {
                encapsulation = SubinterfaceConfig.Encapsulation.isl;
            }
            String subinterfaceLinkLocal;
            if (subinterfaceAutoLinkLocalCheckBox.isSelected())
            {
                subinterfaceLinkLocal = "Auto";
            }
            else
            {
                subinterfaceLinkLocal = verify.verifyNonObligatoryIPv6(subinterfaceIPv6LinkLocalTextField);
            }
            if (verify.isFlawless)
            {
                boolean isDuplicate = false;
                for (SubinterfaceData row : data)
                {
                    if (row.vlan == subinterfaceVlan)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The subinterface can't be created because there is already one with this VLAN");
                        alert.setHeaderText("Subinterface can't be created");
                        alert.show();
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    data.add(new SubinterfaceData(subinterfaceVlan, encapsulation, subinterfaceIpv4Address, subinterfaceIpv4Mask, subinterfaceIpv6Address, subinterfaceIpv6Prefix, subinterfaceLinkLocal, iface));
                }
            }

            subinterfacesTable.setItems(data);
        });

        removeSubinterface.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = subinterfacesTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                Interface subinterface = subinterfacesTable.getSelectionModel().getSelectedItem().subinterface;
                for (int i = 0; i < interfaces.size(); i++)
                {
                    if (interfaces.get(i).equals(subinterface))
                    {
                        interfaces.remove(i);
                        break;
                    }
                }
                data.remove(row);
            }
        });

        return new GroupBox("Subinterfaces", box);
    }

    @Contract(" -> new")
    private @NotNull Node constructIPv6Pane()
    {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(15);

        Label ipv6Address = new Label("IP Address");
        Label ipv6Prefix = new Label("/");
        Label ipv6LinkLocal = new Label("Link-Local");
        Label autoGenerateLinkLocal = new Label("Autogenerate Link-Local address");

        ipv6AddressTextField = new TextField();
        ipv6AddressTextField.setId("IPv6 address");
        ipv6PrefixTextField = new TextField();
        ipv6PrefixTextField.setPrefSize(35, 15);
        ipv6PrefixTextField.setId("IPv6 prefix");
        ipv6LinkLocalTextField = new TextField();
        ipv6LinkLocalTextField.setId("IPv6 link-local");

        autoLinkLocalCheckBox = new CheckBox();

        ipv6LinkLocalTextField.editableProperty().bind(autoLinkLocalCheckBox.selectedProperty().not());
        ipv6LinkLocalTextField.disableProperty().bind(autoLinkLocalCheckBox.selectedProperty());

        gridPane.add(ipv6Address, 0, 0);
        gridPane.add(ipv6Prefix, 2, 0);
        gridPane.add(ipv6LinkLocal, 0, 1);
        gridPane.add(autoGenerateLinkLocal, 0, 2);

        gridPane.add(ipv6AddressTextField, 1, 0);
        gridPane.add(ipv6PrefixTextField, 3, 0);
        gridPane.add(ipv6LinkLocalTextField, 1, 1);

        gridPane.add(autoLinkLocalCheckBox, 1, 2);
        gridPane.setPadding(new Insets(15));

        return new GroupBox("IPv6", gridPane);
    }

    @Contract(" -> new")
    private @NotNull Node constructIPv4Pane()
    {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(15);

        Label ipv4Address = new Label("IP Address");
        Label ipv4Mask = new Label("Mask/Prefix");

        ipv4AddressTextField = new TextField();
        ipv4AddressTextField.setId("IPv4 address");
        ipv4MaskTextField = new TextField();
        ipv4MaskTextField.setId("IPv4 mask");

        gridPane.add(ipv4Address, 0, 0);
        gridPane.add(ipv4Mask, 0, 1);
        gridPane.add(ipv4AddressTextField, 1, 0);
        gridPane.add(ipv4MaskTextField, 1, 1);

        gridPane.setPadding(new Insets(15));

        return new GroupBox("IPv4", gridPane);
    }

    public class SubinterfaceData
    {
        private Subinterface subinterface;
        private int vlan;
        private SubinterfaceConfig.Encapsulation encapsulation;
        private String ipv4Network;
        private String ipv6Network;
        private String linkLocal;
        private boolean autoLinkLocal;
        private IPv4Network subinterfaceIpv4Network;
        private IPv6Network subinterfaceIpv6Network;

        public SubinterfaceData(int vlan, SubinterfaceConfig.Encapsulation encapsulation, String ipv4Address, String ipv4AddressMask, String ipv6Address, String ipv6AddressPrefix, String linkLocal, Interface hostInterface)
        {
            this.vlan = vlan;
            this.encapsulation = encapsulation;

            this.linkLocal = linkLocal;
            if (ipv4Address != null && ipv4AddressMask != null && !Objects.equals(ipv4Address, "") && !Objects.equals(ipv4AddressMask, ""))
            {
                this.subinterfaceIpv4Network = new IPv4Network(ipv4Address, ipv4AddressMask);
                this.ipv4Network = subinterfaceIpv4Network.getIPAddress() + subinterfaceIpv4Network.getPrefix();
            }
            else
            {
                this.ipv4Network = ipv4Address;
            }
            autoLinkLocal = linkLocal.equals("Auto");
            //Check if mask or address is legit
            if (ipv6Address != null && ipv6AddressPrefix != null && !Objects.equals(ipv6Address, "") && !Objects.equals(ipv6AddressPrefix, ""))
            {
                //Mask and address is legit, but is link-local legit
                if (!linkLocal.equals("") && !autoLinkLocal)
                {
                    //Everything is legit
                    this.subinterfaceIpv6Network = new IPv6Network(ipv6Address, ipv6AddressPrefix, linkLocal);
                }
                else
                {
                    //Link local was not legit,but the address and prefix was
                    this.subinterfaceIpv6Network = new IPv6Network(ipv6Address, ipv6AddressPrefix, null);
                }
                this.ipv6Network = subinterfaceIpv6Network.getIPAddress() + subinterfaceIpv6Network.getPrefix();
            }
            else
            {
                //Address and prefix is not legit, but link local could be
                if (!linkLocal.equals(""))
                {
                    //Link local is legit but should it be Auto?
                    if (autoLinkLocal)
                    {
                        this.subinterfaceIpv6Network = new IPv6Network(null);
                    }
                    else
                    {
                        this.subinterfaceIpv6Network = new IPv6Network(linkLocal);
                    }
                }
                this.ipv6Network = ipv6Address;
            }
            subinterface = new Subinterface(hostInterface, vlan);
            interfaces.add(subinterface);
        }

        public Subinterface getSubinterface()
        {
            return subinterface;
        }

        public int getVlan()
        {
            return vlan;
        }

        public SubinterfaceConfig.Encapsulation getEncapsulation()
        {
            return encapsulation;
        }

        public String getIpv4Network()
        {
            return ipv4Network;
        }


        public String getIpv6Network()
        {
            return ipv6Network;
        }


        public String getLinkLocal()
        {
            return linkLocal;
        }

        public IPv4Network getSubinterfaceIpv4Network()
        {
            return subinterfaceIpv4Network;
        }

        public IPv6Network getSubinterfaceIpv6Network()
        {
            return subinterfaceIpv6Network;
        }

        public boolean isAutoLinkLocal()
        {
            return autoLinkLocal;
        }
    }
}
