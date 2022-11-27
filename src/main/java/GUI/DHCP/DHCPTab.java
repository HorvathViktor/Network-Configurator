package main.java.GUI.DHCP;

import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;

public class DHCPTab extends BasicTab
{
    public DHCPPane dhcpPane;
    public DHCPv6Pane dhcPv6Pane;
    public DHCPRelayPane dhcpRelayPane;

    public DHCPTab(ObservableList<Interface> interfaces)
    {
        super("DHCP");

        VBox box = new VBox();
        box.setSpacing(20);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        dhcpPane = new DHCPPane();
        box.getChildren().add(dhcpPane.constructPane());

        dhcPv6Pane = new DHCPv6Pane();
        box.getChildren().add(dhcPv6Pane.constructPane(interfaces));

        dhcpRelayPane = new DHCPRelayPane();
        box.getChildren().add(dhcpRelayPane.constructDHCPRelayPane(interfaces));

        anchorPane.getChildren().add(box);
    }
}
