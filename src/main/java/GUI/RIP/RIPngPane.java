package main.java.GUI.RIP;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Component.OnOffSwitch;

import java.util.Objects;

public class RIPngPane
{
    public TextField ripngIDTextField;
    public CheckBox redistributeStaticCheckBox;
    public OnOffSwitch processSwitch;
    public ObservableList<Interface> advertisedInterfaces = FXCollections.observableArrayList();

    protected Node constructPane(ObservableList<Interface> interfaces)
    {

        GridPane gridPane = new GridPane();
        gridPane.setHgap(-100);
        gridPane.setVgap(30);
        Label enableRIP = new Label("Enable RIPng process");
        Label ripngID = new Label("RIP ID");
        Label redistributeStatic = new Label("Redistribute Static");

        processSwitch = new OnOffSwitch();

        ripngIDTextField = new TextField();
        ripngIDTextField.setId("RIPng ID text field");

        redistributeStaticCheckBox = new CheckBox();

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.getChildren().add(enableRIP);
        flowPane.getChildren().add(processSwitch);


        gridPane.add(flowPane, 0, 0);


        gridPane.add(ripngID, 0, 1);
        gridPane.add(ripngIDTextField, 1, 1);


        gridPane.add(redistributeStatic, 0, 2);
        gridPane.add(redistributeStaticCheckBox, 1, 2);


        GridPane table = new GridPane();
        table.setHgap(100);
        table.setVgap(35);

        Label interfaceLabel = new Label("Interfaces");

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        ComboBox<Interface> interfacesComboBox = new ComboBox<>(interfaces);
        interfacesComboBox.getSelectionModel().selectFirst();



        table.add(interfaceLabel, 0, 0);


        table.add(interfacesComboBox, 0, 1);

        table.add(add, 0, 2);
        table.add(remove, 2, 2);

        ListView<String> advertisedInterfacesList = new ListView<>();

        advertisedInterfacesList.setPrefSize(400, 300);
        advertisedInterfacesList.autosize();

        table.add(advertisedInterfacesList, 0, 3, 5, 3);

        add.setOnAction(event ->
        {
            Interface iface = interfacesComboBox.getSelectionModel().getSelectedItem();
            boolean isDuplicate = false;
            for (String row : advertisedInterfacesList.getItems())
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
                advertisedInterfacesList.getItems().add(iface.getInterface());
                advertisedInterfaces.add(iface);
            }
        });

        remove.setOnAction(event ->
        {
            String item = advertisedInterfacesList.getSelectionModel().getSelectedItem();
            if (advertisedInterfacesList.getItems().size() != 0)
            {
                for (int i = 0; i < advertisedInterfacesList.getItems().size(); i++)
                {
                    if (advertisedInterfacesList.getItems().get(i).equals(item))
                    {
                        advertisedInterfacesList.getItems().remove(i);
                        advertisedInterfaces.remove(i);
                        break;
                    }
                }
            }
        });

        HBox box = new HBox();
        box.setPadding(new Insets(15));
        box.setSpacing(115);
        box.getChildren().add(gridPane);
        box.getChildren().add(table);

        return new GroupBox("RIPng", box);
    }
}
