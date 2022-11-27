package main.java.GUI.Static;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.*;
import main.java.GUI.BasicTab;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Input.InPaneVerification;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StaticRouteTab extends BasicTab
{
    public ObservableList<StaticRoutesData> data = FXCollections.observableArrayList();

    public StaticRouteTab(ObservableList<Interface> interfaces)
    {
        super("Static Routing");
        constructPane(interfaces);
    }

    private void constructPane(ObservableList<Interface> interfaces)
    {

        HBox box = new HBox();

        GridPane westGrid = new GridPane();
        westGrid.setVgap(50);
        westGrid.setHgap(50);
        westGrid.setPadding(new Insets(20, 20, 20, 20));

        GridPane eastGrid = new GridPane();
        eastGrid.setVgap(50);
        eastGrid.setHgap(50);
        eastGrid.setPadding(new Insets(20, 20, 20, 20));

        Label destinationNetworkLabel = new Label("Destination Network");
        Label maskOrPrefixLabel = new Label("Mask/Prefix");
        Label nextHopLabel = new Label("Next hop");
        Label interfaceLabel = new Label("Interfaces");
        Label administrativeDistanceLabel = new Label("Administrative distance");

        TextField destinationNetworkTextField = new TextField();
        TextField maskOrPrefixTextField = new TextField();
        TextField nextHopTextField = new TextField();
        TextField administrativeDistanceTextField = new TextField();

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        ComboBox<Interface> interfaceComboBox = new ComboBox<>(interfaces);
        interfaceComboBox.getSelectionModel().selectFirst();

        TableView<StaticRoutesData> staticRoutesTable = new TableView<>(data);
        staticRoutesTable.setPrefSize(500, 250);
        staticRoutesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StaticRoutesData, String> destinationNetworkColumn;
        TableColumn<StaticRoutesData, String> typeColumn;
        TableColumn<StaticRoutesData, String> typeDataColumn;
        TableColumn<StaticRoutesData, String> administrativeDistanceColumn;

        TableColumn<StaticRoutesData, String>[] columns = new TableColumn[]
                {
                        destinationNetworkColumn = new TableColumn<>("Destination Network"),
                        typeColumn = new TableColumn<>("Type"),
                        typeDataColumn = new TableColumn<>("Type data"),
                        administrativeDistanceColumn = new TableColumn<>("Administrative Distance"),
                };

        String[] columnData = new String[]
                {
                        "destinationNet",
                        "routeType",
                        "routeData",
                        "administrativeDistance",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        staticRoutesTable.getColumns().addAll(destinationNetworkColumn, typeColumn, typeDataColumn, administrativeDistanceColumn);

        westGrid.add(destinationNetworkLabel, 0, 0);
        westGrid.add(maskOrPrefixLabel, 0, 1);
        westGrid.add(nextHopLabel, 0, 2);
        westGrid.add(interfaceLabel, 0, 3);
        westGrid.add(administrativeDistanceLabel, 0, 4);

        westGrid.add(destinationNetworkTextField, 1, 0);
        westGrid.add(maskOrPrefixTextField, 1, 1);
        westGrid.add(nextHopTextField, 1, 2);
        westGrid.add(interfaceComboBox, 1, 3);
        westGrid.add(administrativeDistanceTextField, 1, 4);

        eastGrid.add(add, 0, 0);
        eastGrid.add(remove, 2, 0);
        eastGrid.add(staticRoutesTable, 0, 1, 3, 4);

        box.getChildren().add(westGrid);
        box.getChildren().add(eastGrid);

        GroupBox groupBox = new GroupBox("Static Routing", box);

        AnchorPane.setTopAnchor(groupBox, 0.0);
        AnchorPane.setRightAnchor(groupBox, 0.0);
        AnchorPane.setBottomAnchor(groupBox, 0.0);
        AnchorPane.setLeftAnchor(groupBox, 0.0);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();
            IPNetwork destinationNet = verify.verifyObligatoryNetwork(destinationNetworkTextField,maskOrPrefixTextField);
            String nextHopNetwork = verify.verifyNonObligatoryIP(nextHopTextField);
            Interface directInterface = interfaceComboBox.getSelectionModel().getSelectedItem();
            int adminDistance = verify.verifyNonObligatoryNumberInput(administrativeDistanceTextField);

            if(destinationNet instanceof IPv4Network)
            {
                if (adminDistance > 255 || adminDistance < 1)
                {
                    adminDistance = 1;
                }
            }
            else
            {
                if (adminDistance > 254 || adminDistance < 1)
                {
                    adminDistance = 1;
                }
            }

            IPNetwork nextHopNet = null;
            if (!Objects.equals(nextHopNetwork, ""))
            {
                try
                {
                    IPAddressValidator.isValidIPv4(nextHopNetwork);
                    nextHopNet = new IPv4Network(nextHopNetwork, "32");
                }
                catch (InvalidIPException e)
                {
                    nextHopNet = new IPv6Network(nextHopNetwork, "64");
                }
                if(!destinationNet.getClass().equals(nextHopNet.getClass()))
                {
                    verify.isFlawless = false;
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The destination network and the next hop network were not from the same address family");
                    alert.setHeaderText("Can't add static route");
                    alert.show();
                }
            }

            if (verify.isFlawless)
            {


                boolean isDuplicate = false;
                for (StaticRoutesData row : data)
                {
                    if (nextHopNet != null)
                    {
                        if (row.nextHopNetwork != null && Objects.equals(row.destinationNetwork.getIPAddress(), destinationNet.getIPAddress()) && Objects.equals(row.destinationNetwork.getPrefix(), destinationNet.getPrefix())
                                && Objects.equals(row.nextHopNetwork.getIPAddress(), nextHopNet.getIPAddress()))
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "This network already exists with this next hop");
                            alert.setHeaderText("Can't add static route");
                            alert.show();
                            isDuplicate = true;
                            break;
                        }
                    }
                    else
                    {
                        if (Objects.equals(row.destinationNetwork.getIPAddress(), destinationNet.getIPAddress()) && Objects.equals(row.destinationNetwork.getPrefix(), destinationNet.getPrefix())
                                && row.directInterface == directInterface)
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "This network already exists with this outward interface");
                            alert.setHeaderText("Can't add static route");
                            alert.show();
                            isDuplicate = true;
                            break;
                        }
                    }
                }
                if (!isDuplicate)
                {
                    if (nextHopNet != null)
                    {
                        data.add(new StaticRoutesData(destinationNet, adminDistance, nextHopNet));
                    }
                    else
                    {
                        data.add(new StaticRoutesData(destinationNet, adminDistance, directInterface));
                    }

                }
            }

        });

        remove.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = staticRoutesTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                data.remove(row);
            }
        });

        anchorPane.getChildren().add(groupBox);
    }

    public static class StaticRoutesData
    {
        private IPNetwork destinationNetwork;
        private int administrativeDistance;
        private IPNetwork nextHopNetwork;
        private Interface directInterface;
        private String destinationNet;
        private String routeType;
        private String routeData;

        public StaticRoutesData(@NotNull IPNetwork destinationNetwork, int administrativeDistance, Interface directInterface)
        {
            this.destinationNetwork = destinationNetwork;
            this.administrativeDistance = administrativeDistance;
            this.directInterface = directInterface;
            destinationNet = destinationNetwork.getIPAddress() + destinationNetwork.getPrefix();
            routeType = "Direct Interface";
            routeData = directInterface.getInterface();
        }

        public StaticRoutesData(@NotNull IPNetwork destinationNetwork, int administrativeDistance, IPNetwork nextHopNetwork)
        {
            this.destinationNetwork = destinationNetwork;
            this.administrativeDistance = administrativeDistance;
            this.nextHopNetwork = nextHopNetwork;
            destinationNet = destinationNetwork.getIPAddress() + destinationNetwork.getPrefix();
            routeType = "Next hop";
            routeData = nextHopNetwork.getIPAddress();
        }

        public IPNetwork getDestinationNetwork()
        {
            return destinationNetwork;
        }

        public int getAdministrativeDistance()
        {
            return administrativeDistance;
        }

        public IPNetwork getNextHopNetwork()
        {
            return nextHopNetwork;
        }

        public Interface getDirectInterface()
        {
            return directInterface;
        }

        public String getDestinationNet()
        {
            return destinationNet;
        }

        public String getRouteType()
        {
            return routeType;
        }

        public String getRouteData()
        {
            return routeData;
        }
    }
}
