package main.java.GUI.Redundancy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.*;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.OSPF.OSPFv2Pane;
import main.java.GUI.Input.InPaneVerification;
import org.jetbrains.annotations.NotNull;

public class VRRPPane
{
    public ComboBox<Interface> interfacesComboBox;

    public TextField groupTextField;
    public TextField priorityTextField;
    public TextField virtualIPTextField;
    public CheckBox preemptCheckBox;

    public ObservableList<VRRPData> data = FXCollections.observableArrayList();


    public Node constructPane(ObservableList<Interface> interfaces)
    {
        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(30);
        box.getChildren().add(constructWestPane(interfaces));
        box.getChildren().add(constructEastPane());

        return new GroupBox("VRRP", box);
    }


    @SuppressWarnings("unchecked")
    private @NotNull Node constructEastPane()
    {
        GridPane eastGrid = new GridPane();
        eastGrid.setHgap(50);
        eastGrid.setVgap(10);

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        TableView<VRRPData> vrrpTable = new TableView<>(data);

        vrrpTable.setPrefSize(500, 250);
        vrrpTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<VRRPData, String> interfaceColumn;
        TableColumn<VRRPData, String> groupColumn;
        TableColumn<VRRPData, String> priorityColumn;
        TableColumn<VRRPData, String> virtualIPColumn;
        TableColumn<VRRPData, String> preemptColumn;

        TableColumn<OSPFv2Pane.OSPFv2Data, String>[] columns = new TableColumn[]
                {
                        interfaceColumn = new TableColumn<>("Interface"),
                        groupColumn = new TableColumn<>("Group"),
                        priorityColumn = new TableColumn<>("Priority"),
                        virtualIPColumn = new TableColumn<>("Virtual IP"),
                        preemptColumn = new TableColumn<>("Preempt"),
                };

        String[] columnData = new String[]
                {
                        "iface",
                        "group",
                        "prio",
                        "ip",
                        "preempt",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        vrrpTable.getColumns().addAll(interfaceColumn, groupColumn, priorityColumn, virtualIPColumn, preemptColumn);

        eastGrid.add(add, 0, 0);
        eastGrid.add(remove, 3, 0);

        eastGrid.add(vrrpTable, 0, 1, 6, 5);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();
            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            boolean preempt = preemptCheckBox.isSelected();
            int group = verify.verifyObligatoryNumberInput(groupTextField);
            int priority = verify.verifyNonObligatoryNumberInput(priorityTextField);
            String virtualIp = verify.verifyObligatoryIPv4(virtualIPTextField);
            if (verify.isFlawless)
            {
                IPv4Network virtualNet = new IPv4Network(virtualIp, "32");
                boolean isDuplicate = false;
                for (VRRPData element : data)
                {
                    if (element.iface.equals(iface) && element.group == group)
                    {
                        isDuplicate = true;
                        Alert alert = new Alert(Alert.AlertType.ERROR, "This interface with this group has already been configured");
                        alert.setHeaderText("Can't create VRRP process");
                        alert.show();
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    data.add(new VRRPData(iface, group, priority, virtualNet, preempt));
                }
            }
        });

        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = vrrpTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                data.remove(row);
            }
        });

        return eastGrid;
    }

    private @NotNull Node constructWestPane(ObservableList<Interface> interfaces)
    {
        GridPane westGrid = new GridPane();
        westGrid.setHgap(10);
        westGrid.setVgap(30);


        Label interfacesLabel = new Label("Interface");
        Label group = new Label("Group");
        Label priority = new Label("Priority");
        Label virtualIP = new Label("Virtual IP");
        Label preempt = new Label("Preempt");


        groupTextField = new TextField();
        priorityTextField = new TextField();
        virtualIPTextField = new TextField();


        preemptCheckBox = new CheckBox();

        interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();

        westGrid.add(interfacesLabel, 0, 0);
        westGrid.add(group, 0, 1);
        westGrid.add(priority, 0, 2);
        westGrid.add(virtualIP, 0, 3);
        westGrid.add(preempt, 0, 4);


        westGrid.add(interfacesComboBox, 1, 0);
        westGrid.add(groupTextField, 1, 1);
        westGrid.add(priorityTextField, 1, 2);
        westGrid.add(virtualIPTextField, 1, 3);
        westGrid.add(preemptCheckBox, 1, 4);

        return westGrid;
    }

    public static class VRRPData
    {
        private Interface iface;
        private int group;
        private int priority;
        private String prio;
        private IPv4Network virtualIp;
        private String ip;
        private boolean isPreempt;
        private String preempt;

        public VRRPData(Interface iface, int group, int priority, IPv4Network virtualIp, boolean isPreempt)
        {
            this.iface = iface;
            this.group = group;
            this.priority = priority;
            prio = priority == -1 ? "Default" : String.valueOf(this.priority);
            this.virtualIp = virtualIp;
            ip = virtualIp.getIPAddress();
            this.isPreempt = isPreempt;
            preempt = isPreempt ? "On" : "Off";
        }

        public Interface getIface()
        {
            return iface;
        }

        public int getGroup()
        {
            return group;
        }

        public int getPriority()
        {
            return priority;
        }

        public String getPrio()
        {
            return prio;
        }

        public IPv4Network getVirtualIp()
        {
            return virtualIp;
        }

        public String getIp()
        {
            return ip;
        }

        public boolean isPreempt()
        {
            return isPreempt;
        }

        public String getPreempt()
        {
            return preempt;
        }
    }
}
