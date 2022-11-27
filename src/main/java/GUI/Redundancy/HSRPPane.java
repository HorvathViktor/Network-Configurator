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

public class HSRPPane
{
    public ComboBox<Interface> interfacesComboBox;

    public TextField groupTextField;
    public TextField priorityTextField;
    public TextField virtualIPTextField;

    public CheckBox autogenerateCheckBox;
    public CheckBox preemptCheckBox;
    public CheckBox version2CheckBox;

    public ObservableList<HSRPData> data = FXCollections.observableArrayList();


    public Node constructPane(ObservableList<Interface> interfaces)
    {
        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(30);
        box.getChildren().add(constructWestPane(interfaces));
        box.getChildren().add(constructEastPane());

        return new GroupBox("HSRP", box);
    }


    @SuppressWarnings("unchecked")
    private @NotNull Node constructEastPane()
    {
        GridPane eastGrid = new GridPane();
        eastGrid.setHgap(50);
        eastGrid.setVgap(10);

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        TableView<HSRPData> hsrpTable = new TableView<>(data);

        hsrpTable.setPrefSize(500, 250);
        hsrpTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HSRPData, String> interfaceColumn;
        TableColumn<HSRPData, String> groupColumn;
        TableColumn<HSRPData, String> priorityColumn;
        TableColumn<HSRPData, String> virtualIPColumn;
        TableColumn<HSRPData, String> preemptColumn;
        TableColumn<HSRPData, String> version2Column;

        TableColumn<OSPFv2Pane.OSPFv2Data, String>[] columns = new TableColumn[]
                {
                        interfaceColumn = new TableColumn<>("Interface"),
                        groupColumn = new TableColumn<>("Group"),
                        priorityColumn = new TableColumn<>("Priority"),
                        virtualIPColumn = new TableColumn<>("Virtual IP"),
                        preemptColumn = new TableColumn<>("Preempt"),
                        version2Column = new TableColumn<>("Version 2"),
                };

        String[] columnData = new String[]
                {
                        "iface",
                        "group",
                        "prio",
                        "ip",
                        "preempt",
                        "v2"
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        hsrpTable.getColumns().addAll(interfaceColumn, groupColumn, priorityColumn, virtualIPColumn, preemptColumn, version2Column);


        eastGrid.add(add, 0, 0);
        eastGrid.add(remove, 3, 0);

        eastGrid.add(hsrpTable, 0, 1, 6, 5);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();
            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            boolean preempt = preemptCheckBox.isSelected();
            boolean version2 = version2CheckBox.isSelected();
            int group = verify.verifyObligatoryNumberInput(groupTextField);
            int priority = verify.verifyNonObligatoryNumberInput(priorityTextField);
            String virtualIp;
            boolean auto = autogenerateCheckBox.isSelected();
            if (auto)
            {
                virtualIp = "Auto";
                verify.makeTextFieldNormal(virtualIPTextField);
            }
            else
            {
                virtualIp = verify.verifyObligatoryIP(virtualIPTextField);
            }
            if (verify.isFlawless)
            {
                IPNetwork virtualNet = null;
                if (!auto)
                {
                    try
                    {
                        IPAddressValidator.isValidIPv4(virtualIp);
                        virtualNet = new IPv4Network(virtualIp, "32");

                    }
                    catch (InvalidIPException e)
                    {
                        virtualNet = new IPv6Network(virtualIp);
                    }
                }

                if (!version2 && virtualNet instanceof IPv6Network)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Version 2 is required for IPv6 HSRP operations");
                    alert.setHeaderText("Can't create HSRP process");
                    alert.show();
                }
                else
                {
                    boolean isDuplicate = false;
                    for (HSRPData element : data)
                    {
                        if (element.iface.equals(iface) && element.group == group)
                        {
                            isDuplicate = true;
                            Alert alert = new Alert(Alert.AlertType.ERROR, "This interface with this group has already been configured");
                            alert.setHeaderText("Can't create HSRP process");
                            alert.show();
                            break;
                        }
                    }
                    if (!isDuplicate)
                    {
                        data.add(new HSRPData(iface, group, priority, virtualNet, preempt, version2));
                    }
                }
            }
        });
        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = hsrpTable.getSelectionModel().getSelectedCells();
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
        Label version2 = new Label("Version 2");


        groupTextField = new TextField();
        priorityTextField = new TextField();
        virtualIPTextField = new TextField();


        autogenerateCheckBox = new CheckBox("Autogenerate (IPv6 only)");
        preemptCheckBox = new CheckBox();
        version2CheckBox = new CheckBox();

        interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();

        virtualIPTextField.editableProperty().bind(autogenerateCheckBox.selectedProperty().not());
        virtualIPTextField.disableProperty().bind(autogenerateCheckBox.selectedProperty());

        westGrid.add(interfacesLabel, 0, 0);
        westGrid.add(group, 0, 1);
        westGrid.add(priority, 0, 2);
        westGrid.add(virtualIP, 0, 3);
        westGrid.add(preempt, 0, 4);
        westGrid.add(version2, 0, 5);

        westGrid.add(autogenerateCheckBox, 2, 3);
        westGrid.add(version2CheckBox, 1, 5);
        westGrid.add(interfacesComboBox, 1, 0);
        westGrid.add(groupTextField, 1, 1);
        westGrid.add(priorityTextField, 1, 2);
        westGrid.add(virtualIPTextField, 1, 3);
        westGrid.add(preemptCheckBox, 1, 4);

        return westGrid;
    }

    public static class HSRPData
    {
        private Interface iface;
        private int group;
        private int priority;
        private String prio;
        private IPNetwork virtualIp;
        private String ip;
        private boolean isPreempt;
        private String preempt;
        private boolean isV2;
        private String v2;

        public HSRPData(Interface iface, int group, int priority, IPNetwork virtualIp, boolean isPreempt, boolean isV2)
        {
            this.iface = iface;
            this.group = group;
            this.priority = priority;
            prio = priority == -1 ? "Default" : String.valueOf(this.priority);
            this.virtualIp = virtualIp;
            if (virtualIp != null)
            {
                if (virtualIp instanceof IPv4Network)
                {
                    ip = virtualIp.getIPAddress();
                }
                else
                {
                    if (virtualIp instanceof IPv6Network && virtualIp.getIPAddress() == null)
                    {
                        ip = ((IPv6Network) virtualIp).getLinkLocalAddress();
                    }
                    else
                    {
                        ip = virtualIp.getIPAddress();
                    }

                }
            }
            else
            {
                ip = "Auto";
            }

            this.isPreempt = isPreempt;
            preempt = isPreempt ? "On" : "Off";
            this.isV2 = isV2;
            v2 = isV2 ? "Enabled" : "Disabled";
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

        public IPNetwork getVirtualIp()
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

        public boolean isV2()
        {
            return isV2;
        }

        public String getV2()
        {
            return v2;
        }
    }
}
