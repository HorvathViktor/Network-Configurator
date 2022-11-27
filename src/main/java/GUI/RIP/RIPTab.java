package main.java.GUI.RIP;

import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;

public class RIPTab extends BasicTab
{
    public RIPPane ripPane;
    public RIPngPane ripngPane;

    public RIPTab(ObservableList<Interface> interfaces)
    {
        super("RIP");

        VBox box = new VBox();
        box.setSpacing(20);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        ripPane = new RIPPane();
        box.getChildren().add(ripPane.constructPane(interfaces));

        ripngPane = new RIPngPane();
        box.getChildren().add(ripngPane.constructPane(interfaces));

        anchorPane.getChildren().add(box);
    }
}
