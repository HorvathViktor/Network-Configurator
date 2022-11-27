package main.java.GUI.Other;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Component.OnOffSwitch;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OtherTab extends BasicTab
{
    public OnOffSwitch enableCDPSwitch;
    public CheckBox cdpMismatchesCheckbox;
    public CheckBox version2Checkbox;
    public ComboBox<Interface> sourceInterfaceComboBox;
    public ObservableList<Interface> enabledInterfaces = FXCollections.observableArrayList();

    public OtherTab(ObservableList<Interface> interfaces)
    {
        super("Other");
        VBox box = new VBox();
        box.setSpacing(20);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        box.getChildren().add(constructDCPPane(interfaces));

        anchorPane.getChildren().add(box);

    }
    @Contract("_ -> new")
    private @NotNull Node constructDCPPane(ObservableList<Interface> interfaces)
    {
        HBox box = new HBox();
        box.setSpacing(10);

        GridPane westGrid = new GridPane();
        westGrid.setVgap(25);
        westGrid.setHgap(35);
        westGrid.setPadding(new Insets(20));

        Label enable = new Label("Enable CDP");
        Label mismatch = new Label("Log mismatches");
        Label version2 = new Label("Version 2");

        enableCDPSwitch = new OnOffSwitch();

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(enableCDPSwitch);

        sourceInterfaceComboBox = new ComboBox<>();
        sourceInterfaceComboBox.getItems().addAll(interfaces);
        sourceInterfaceComboBox.getSelectionModel().selectFirst();

        CheckBox specifySourceInterfaceCheckBox = new CheckBox("Specify source interface");
        cdpMismatchesCheckbox = new CheckBox();
        version2Checkbox = new CheckBox();

        westGrid.add(enable,0,0);
        westGrid.add(mismatch,0,2);
        westGrid.add(version2,0,3);

        westGrid.add(flowPane,1,0);

        westGrid.add(sourceInterfaceComboBox,1,1);

        westGrid.add(specifySourceInterfaceCheckBox,0,1);
        westGrid.add(cdpMismatchesCheckbox,1,2);
        westGrid.add(version2Checkbox,1,3);

        GridPane eastGrid = new GridPane();
        eastGrid.setHgap(100);
        eastGrid.setVgap(35);
        eastGrid.setPadding(new Insets(20));

        Label interfacesLabel = new Label("Interfaces");
        Label warningLabel = new Label("Note: only interfaces added will use CDP");

        ComboBox<Interface> addCDPComboBox = new ComboBox<>(interfaces);
        addCDPComboBox.getSelectionModel().selectFirst();

        Button add = new Button("Enable");
        Button remove = new Button("Disable");

        eastGrid.add(interfacesLabel, 0, 0);
        eastGrid.add(addCDPComboBox,1,0);
        eastGrid.add(warningLabel,2,0);

        ListView<String> cdpTable = new ListView<>();

        cdpTable.setPrefSize(400, 300);
        cdpTable.autosize();

        eastGrid.add(add,0,1);
        eastGrid.add(remove,2,1);

        eastGrid.add(cdpTable,0,2,4,3);

        box.getChildren().add(westGrid);
        box.getChildren().add(eastGrid);

        add.setOnAction(event ->
        {
            Interface iface = addCDPComboBox.getSelectionModel().getSelectedItem();
            boolean isDuplicate = false;
            for (String row : cdpTable.getItems())
            {
                if (Objects.equals(row, iface.getInterface()))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Can't add this interface because it was already added!");
                    alert.setHeaderText("Interface can't be added");
                    alert.show();
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate)
            {
                cdpTable.getItems().add(iface.getInterface());
                enabledInterfaces.add(iface);
            }
        });

        remove.setOnAction(event ->
        {
            String item = cdpTable.getSelectionModel().getSelectedItem();
            if (cdpTable.getItems().size() != 0)
            {
                for (int i = 0; i < cdpTable.getItems().size(); i++)
                {
                    if (cdpTable.getItems().get(i).equals(item))
                    {
                        cdpTable.getItems().remove(i);
                        enabledInterfaces.remove(i);
                        break;
                    }
                }
            }
        });

        return new GroupBox("CDP",box);
    }
}
