package main.java.GUI.Interface;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import main.java.BasicNetworking.Interface;
import main.java.GUI.BasicTab;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class InterfacesTab extends BasicTab
{
    public InterfacesPane[] panes;

    public InterfacesTab(@NotNull ObservableList<Interface> interfaces)
    {
        super("Interfaces");

        VBox box = new VBox();
        box.setPadding(new Insets(15));
        box.setSpacing(50);
        panes = new InterfacesPane[interfaces.size()];
        for (int i = 0; i < interfaces.size(); i++)
        {
            panes[i] = new InterfacesPane(interfaces);
            box.getChildren().add(panes[i].constructPane(interfaces.get(i)));
            if (i != interfaces.size()-1)
            {
                box.getChildren().add(drawSeparatorLine());
            }
        }
        anchorPane.getChildren().add(box);
    }

    @Contract(" -> new")
    private @NotNull Node drawSeparatorLine()
    {
        Path path = new Path();

        MoveTo moveTo = new MoveTo();
        moveTo.setX(1300);
        moveTo.setY(150.0);

        HLineTo hLineTo = new HLineTo();

        hLineTo.setX(10.0);

        path.getElements().add(moveTo);
        path.getElements().add(hLineTo);
        path.setStroke(Color.BLUE);

        return new Group(path);
    }
}
