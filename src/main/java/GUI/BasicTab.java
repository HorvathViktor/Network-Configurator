package main.java.GUI;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

public abstract class BasicTab extends Tab
{
    private final ScrollPane scrollPane;
    protected AnchorPane anchorPane;

    public BasicTab(String title)
    {
        super(title);
        scrollPane = new ScrollPane();
        anchorPane = new AnchorPane();
        anchorPane.setPadding(new Insets(20));
        this.setContent(createBasicPane());
    }
    private Node createBasicPane()
    {
        scrollPane.setContent(anchorPane);
        return scrollPane;
    }
}
