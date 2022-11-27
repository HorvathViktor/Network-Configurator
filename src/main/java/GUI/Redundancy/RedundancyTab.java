package main.java.GUI.Redundancy;

import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;

public class RedundancyTab extends BasicTab
{
    public HSRPPane HSRPPane;
    public VRRPPane VRRPPane;

    public RedundancyTab(ObservableList<Interface> interfaces)
    {
        super("Redundancy");

        VBox box = new VBox();
        box.setSpacing(20);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        HSRPPane = new HSRPPane();
        box.getChildren().add(HSRPPane.constructPane(interfaces));

        VRRPPane = new VRRPPane();
        box.getChildren().add(VRRPPane.constructPane(interfaces));

        anchorPane.getChildren().add(box);
    }
}
