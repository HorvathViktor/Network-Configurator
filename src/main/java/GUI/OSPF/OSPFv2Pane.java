package main.java.GUI.OSPF;

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
import main.java.GUI.Input.InPaneVerification;

public class OSPFv2Pane
{
    public TextField processIDTextField;
    public TextField routerIDTextField;
    public CheckBox redistributeStaticCheckBox;
    public CheckBox defaultRouteCheckBox;
    public CheckBox loggingCheckBox;
    public OnOffSwitch processSwitch;
    public ObservableList<OSPFv2Data> data = FXCollections.observableArrayList();

    @SuppressWarnings("unchecked")
    protected Node constructPane(ObservableList<Interface> interfaces)
    {
        GridPane westGrid = new GridPane();
        westGrid.setHgap(-100);
        westGrid.setVgap(30);
        Label enableOSPF = new Label("Enable OSPFv2 process");
        Label processID = new Label("Process ID");
        Label routerID = new Label("Router ID");
        Label redistributeStatic = new Label("Redistribute Static");
        Label defaultRoute = new Label("Advertise Default Route");
        Label logging = new Label("Adjacency logging");

        processSwitch = new OnOffSwitch();

        processIDTextField = new TextField();
        processIDTextField.setMaxSize(50, 15);
        processIDTextField.setId("OSPFv2 Process ID text field");
        routerIDTextField = new TextField();
        routerIDTextField.setId("OSPFv2 Router ID text field");

        redistributeStaticCheckBox = new CheckBox();
        defaultRouteCheckBox = new CheckBox();
        loggingCheckBox = new CheckBox();

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.getChildren().add(enableOSPF);
        flowPane.getChildren().add(processSwitch);

        westGrid.add(flowPane, 0, 0);
        westGrid.add(processID, 0, 1);
        westGrid.add(routerID, 0, 2);
        westGrid.add(redistributeStatic, 0, 3);
        westGrid.add(defaultRoute, 0, 4);
        westGrid.add(logging, 0, 5);

        westGrid.add(processIDTextField, 1, 1);
        westGrid.add(routerIDTextField, 1, 2);
        westGrid.add(redistributeStaticCheckBox, 1, 3);
        westGrid.add(defaultRouteCheckBox, 1, 4);
        westGrid.add(loggingCheckBox, 1, 5);

        GridPane eastGrid = new GridPane();
        eastGrid.setHgap(100);
        eastGrid.setVgap(35);

        Label interfacesLabel = new Label("Interfaces");
        Label passiveInterface = new Label("Passive Interface");
        Label areaLabel = new Label("Area");

        ComboBox<Interface> interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();

        CheckBox passiveInterfaceCheckBox = new CheckBox();

        TextField areaTextField = new TextField();
        areaTextField.setMaxSize(50, 15);

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        TableView<OSPFv2Data> advertisedInterfaces = new TableView<>(data);

        advertisedInterfaces.setPrefSize(500, 250);
        advertisedInterfaces.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OSPFv2Data, String> interfaceColumn;
        TableColumn<OSPFv2Data, String> passiveInterfaceColumn;
        TableColumn<OSPFv2Data, String> areaColumn;

        TableColumn<OSPFv2Data, String>[] columns = new TableColumn[]
                {
                        interfaceColumn = new TableColumn<>("Interface"),
                        passiveInterfaceColumn = new TableColumn<>("Passive"),
                        areaColumn = new TableColumn<>("Area"),
                };

        String[] columnData = new String[]
                {
                        "iface",
                        "passive",
                        "area"
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        advertisedInterfaces.getColumns().addAll(interfaceColumn, passiveInterfaceColumn, areaColumn);

        eastGrid.add(interfacesLabel, 0, 0);
        eastGrid.add(passiveInterface, 1, 0);
        eastGrid.add(areaLabel, 2, 0);

        eastGrid.add(interfacesComboBox, 0, 1);
        eastGrid.add(passiveInterfaceCheckBox, 1, 1);
        eastGrid.add(areaTextField, 2, 1);

        eastGrid.add(add, 0, 2);
        eastGrid.add(remove, 2, 2);

        eastGrid.add(advertisedInterfaces, 0, 3, 5, 3);


        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(115);
        box.getChildren().add(westGrid);
        box.getChildren().add(eastGrid);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();

            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            boolean isPassive = passiveInterfaceCheckBox.isSelected();
            int area = verify.verifyObligatoryNumberInput(areaTextField);

            if (verify.isFlawless)
            {
                boolean isDuplicate = false;
                for (OSPFv2Data element : data)
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
                    data.add(new OSPFv2Data(iface, isPassive, area));
                }
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

        return new GroupBox("OSPFv2", box);
    }

    public static class OSPFv2Data
    {
        private Interface iface;
        private boolean isPassive;
        private String passive;
        private int area;

        public OSPFv2Data(Interface iface, boolean isPassive, int area)
        {
            this.iface = iface;
            this.isPassive = isPassive;
            passive = isPassive ? "Yes" : "No";
            this.area = area;
        }

        public Interface getIface()
        {
            return iface;
        }

        public boolean isPassive()
        {
            return isPassive;
        }

        public int getArea()
        {
            return area;
        }

        public String getPassive()
        {
            return passive;
        }
    }
}
