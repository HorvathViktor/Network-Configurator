package main.java.GUI.RIP;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Component.OnOffSwitch;

public class RIPPane
{
    public CheckBox ripVersion2CheckBox;
    public CheckBox redistributeStaticCheckBox;
    public CheckBox defaultRouteAdvertisement;
    public CheckBox autoSummaryCheckBox;
    public OnOffSwitch processSwitch;
    public ObservableList<RIPData> data = FXCollections.observableArrayList();

    @SuppressWarnings("unchecked")
    protected Node constructPane(ObservableList<Interface> interfaces)
    {

        GridPane gridPane = new GridPane();
        gridPane.setHgap(-100);
        gridPane.setVgap(30);
        Label enableRIP = new Label("Enable RIP process");
        Label repVersion2 = new Label("Version 2");
        Label redistributeStatic = new Label("Redistribute Static");
        Label defaultRoute = new Label("Advertise Default Route");
        Label autoSummary = new Label("Auto Summary");

        processSwitch = new OnOffSwitch();


        ripVersion2CheckBox = new CheckBox();
        redistributeStaticCheckBox = new CheckBox();
        defaultRouteAdvertisement = new CheckBox();
        autoSummaryCheckBox = new CheckBox();

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.getChildren().add(enableRIP);
        flowPane.getChildren().add(processSwitch);


        gridPane.add(flowPane, 0, 0);

        gridPane.add(repVersion2, 0, 1);
        gridPane.add(ripVersion2CheckBox, 1, 1);

        gridPane.add(redistributeStatic, 0, 2);
        gridPane.add(redistributeStaticCheckBox, 1, 2);

        gridPane.add(defaultRoute, 0, 3);
        gridPane.add(defaultRouteAdvertisement, 1, 3);

        gridPane.add(autoSummary, 0, 4);
        gridPane.add(autoSummaryCheckBox, 1, 4);

        GridPane table = new GridPane();
        table.setHgap(100);
        table.setVgap(35);

        Label interfaceLabel = new Label("Interfaces");
        Label passiveInterface = new Label("Passive Interface");

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        ComboBox<Interface> interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();

        CheckBox passiveInterfaceCheckBox = new CheckBox();

        TableView<RIPData> advertisedInterfaces = new TableView<>(data);

        advertisedInterfaces.setPrefSize(500, 250);
        advertisedInterfaces.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RIPData, String> interfaceColumn;
        TableColumn<RIPData, String> passiveInterfaceColumn;

        TableColumn<RIPData, String>[] columns = new TableColumn[]
                {
                        interfaceColumn = new TableColumn<>("Interface"),
                        passiveInterfaceColumn = new TableColumn<>("Passive"),
                };

        String[] columnData = new String[]
                {
                        "iface",
                        "passive",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        advertisedInterfaces.getColumns().addAll(interfaceColumn, passiveInterfaceColumn);

        table.add(interfaceLabel, 0, 0);

        table.add(passiveInterface, 1, 0);


        table.add(interfacesComboBox, 0, 1);
        table.add(passiveInterfaceCheckBox, 1, 1);

        table.add(add, 0, 2);
        table.add(remove, 2, 2);

        table.add(advertisedInterfaces, 0, 3, 5, 3);

        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(115);
        box.getChildren().add(gridPane);
        box.getChildren().add(table);

        add.setOnAction(event ->
        {

            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            boolean isPassive = passiveInterfaceCheckBox.isSelected();
            boolean isDuplicate = false;
            for (RIPData element : data)
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
                data.add(new RIPData(iface, isPassive));
            }
        });

        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = advertisedInterfaces.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                data.remove(row);
            }
        });

        return new GroupBox("RIP", box);
    }

    public static class RIPData
    {
        private Interface iface;
        private boolean isPassive;
        private String passive;

        public RIPData(Interface iface, boolean isPassive)
        {
            this.iface = iface;
            this.isPassive = isPassive;
            passive = isPassive ? "Yes" : "No";
        }

        public Interface getIface()
        {
            return iface;
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
