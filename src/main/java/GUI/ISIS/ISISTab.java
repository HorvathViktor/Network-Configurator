package main.java.GUI.ISIS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Component.OnOffSwitch;
import org.jetbrains.annotations.NotNull;

public class ISISTab extends BasicTab
{

    public TextField isisTagTextField;
    public TextField netIDTextField;
    public CheckBox redistributeStaticCheckBox;
    public CheckBox defaultRouteCheckBox;
    public CheckBox loggingCheckBox;
    public OnOffSwitch processSwitch;
    public ObservableList<ISISData> data = FXCollections.observableArrayList();

    public ISISTab(ObservableList<Interface> interfaces)
    {
        super("IS-IS");
        constructPane(interfaces);
    }

    @SuppressWarnings("unchecked")
    private void constructPane(@NotNull ObservableList<Interface> interfaces)
    {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(-100);
        gridPane.setVgap(30);
        Label enableISIS = new Label("Enable IS-IS process");
        Label isisTag = new Label("IS-IS ID");
        Label netID = new Label("Net ID");
        Label redistributeStatic = new Label("Redistribute Static");
        Label defaultRoute = new Label("Advertise Default Route");
        Label logging = new Label("Adjacency logging");

        processSwitch = new OnOffSwitch();

        isisTagTextField = new TextField();
        isisTagTextField.setId("IS-IS tag text field");
        netIDTextField = new TextField();
        netIDTextField.setId("IS-IS NET ID text field");

        redistributeStaticCheckBox = new CheckBox();
        defaultRouteCheckBox = new CheckBox();
        loggingCheckBox = new CheckBox();

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.getChildren().add(enableISIS);
        flowPane.getChildren().add(processSwitch);

        gridPane.add(flowPane, 0, 0);
        gridPane.add(isisTag, 0, 1);
        gridPane.add(netID, 0, 2);
        gridPane.add(redistributeStatic, 0, 3);
        gridPane.add(defaultRoute, 0, 4);
        gridPane.add(logging, 0, 5);

        gridPane.add(isisTagTextField, 1, 1);
        gridPane.add(netIDTextField, 1, 2);
        gridPane.add(redistributeStaticCheckBox, 1, 3);
        gridPane.add(defaultRouteCheckBox, 1, 4);
        gridPane.add(loggingCheckBox, 1, 5);

        GridPane table = new GridPane();
        table.setHgap(100);
        table.setVgap(35);

        Label interfaceLabel = new Label("Interfaces");
        Label advertisev4 = new Label("Advertise IPv4");
        Label advertisev6 = new Label("Advertise IPv6");
        Label passiveInterface = new Label("Passive Interface");

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        ComboBox<Interface> interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();

        CheckBox advertiseIpv4CheckBox = new CheckBox();
        CheckBox advertiseIpv6CheckBox = new CheckBox();
        CheckBox passiveInterfaceCheckBox = new CheckBox();

        TableView<ISISData> isisTable = new TableView<>(data);

        isisTable.setPrefSize(500, 250);
        isisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ISISData, String> interfaceColumn;
        TableColumn<ISISData, String> ipv4Column;
        TableColumn<ISISData, String> ipv6Column;
        TableColumn<ISISData, String> passiveInterfaceColumn;

        TableColumn<ISISData, String>[] columns = new TableColumn[]
                {
                        interfaceColumn = new TableColumn<>("Interface"),
                        ipv4Column = new TableColumn<>("IPv4"),
                        ipv6Column = new TableColumn<>("IPv6"),
                        passiveInterfaceColumn = new TableColumn<>("Passive"),
                };

        String[] columnData = new String[]
                {
                        "iface",
                        "ipv4",
                        "ipv6",
                        "passive"
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        isisTable.getColumns().addAll(interfaceColumn, ipv4Column, ipv6Column, passiveInterfaceColumn);


        table.add(interfaceLabel, 0, 0);
        table.add(advertisev4, 1, 0);
        table.add(advertisev6, 2, 0);
        table.add(passiveInterface, 3, 0);

        table.add(interfacesComboBox, 0, 1);
        table.add(advertiseIpv4CheckBox, 1, 1);
        table.add(advertiseIpv6CheckBox, 2, 1);
        table.add(passiveInterfaceCheckBox, 3, 1);

        table.add(add, 0, 2);
        table.add(remove, 2, 2);

        table.add(isisTable, 0, 3, 5, 3);

        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(115);
        box.getChildren().add(gridPane);
        box.getChildren().add(table);

        GroupBox mainGroup = new GroupBox("IS-IS", box);

        AnchorPane.setTopAnchor(mainGroup, 0.0);
        AnchorPane.setRightAnchor(mainGroup, 0.0);
        AnchorPane.setBottomAnchor(mainGroup, 0.0);
        AnchorPane.setLeftAnchor(mainGroup, 0.0);

        add.setOnAction(event ->
        {

            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            boolean isIpv4 = advertiseIpv4CheckBox.isSelected();
            boolean isIpv6 = advertiseIpv6CheckBox.isSelected();
            boolean isPassive = passiveInterfaceCheckBox.isSelected();

            boolean isDuplicate = false;
            if(!isIpv4 && !isIpv6)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "At lease one advertisement must be chosen!");
                alert.setHeaderText("Can't add advertisement");
                alert.show();
            }
            else
            {
                for (ISISData element : data)
                {
                    if (element.iface.equals(iface))
                    {
                        isDuplicate = true;
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The interface can't be added because it has already been added.");
                        alert.setHeaderText("Can't add interface");
                        alert.show();
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    data.add(new ISISData(iface, isIpv4, isIpv6, isPassive));
                }
            }
        });

        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = isisTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                data.remove(row);
            }
        });

        anchorPane.getChildren().add(mainGroup);
    }

    public static class ISISData
    {
        private Interface iface;
        private boolean isIpv4;
        private String ipv4;
        private boolean isIpv6;
        private String ipv6;
        private boolean isPassive;
        private String passive;

        public ISISData(Interface iface, boolean isIpv4, boolean isIpv6, boolean isPassive)
        {
            this.iface = iface;
            this.isIpv4 = isIpv4;
            ipv4 = isIpv4 ? "Advertised" : "Not advertised";
            this.isIpv6 = isIpv6;
            ipv6 = isIpv6 ? "Advertised" : "Not advertised";
            this.isPassive = isPassive;
            passive = isPassive ? "Yes" : "No";
        }

        public Interface getIface()
        {
            return iface;
        }

        public boolean isIpv4()
        {
            return isIpv4;
        }

        public String getIpv4()
        {
            return ipv4;
        }

        public boolean isIpv6()
        {
            return isIpv6;
        }

        public String getIpv6()
        {
            return ipv6;
        }

        public boolean isPassive()
        {
            return isPassive;
        }

        public String getPassive()
        {
            return passive;
        }
    }
}
