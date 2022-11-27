package main.java.GUI.EIGRP;

import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;

public class EIGRPTab extends BasicTab
{

    public EIGRPv4Pane eigrpV4Pane;
    public EIGRPv6Pane eigrpV6Pane;

    public EIGRPTab(ObservableList<Interface> interfaces)
    {
        super("EIGRP");

        VBox box = new VBox();
        box.setSpacing(20);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        eigrpV4Pane = new EIGRPv4Pane();
        box.getChildren().add(eigrpV4Pane.constructPane(interfaces));

        eigrpV6Pane = new EIGRPv6Pane();
        box.getChildren().add(eigrpV6Pane.constructPane(interfaces));

        anchorPane.getChildren().add(box);
    }
}
