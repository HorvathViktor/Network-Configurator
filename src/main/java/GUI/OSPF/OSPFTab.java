package main.java.GUI.OSPF;

import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;

public class OSPFTab extends BasicTab
{
    public OSPFv2Pane ospfv2Pane;
    public OSPFv3Pane ospfv3Pane;

    public OSPFTab(ObservableList<Interface> interfaces)
    {
        super("OSPF");

        VBox box = new VBox();
        box.setSpacing(20);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        ospfv2Pane = new OSPFv2Pane();
        box.getChildren().add(ospfv2Pane.constructPane(interfaces));

        ospfv3Pane = new OSPFv3Pane();
        box.getChildren().add(ospfv3Pane.constructPane(interfaces));

        anchorPane.getChildren().add(box);
    }
}
