package main.java.GUI.Component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GroupBox extends StackPane
{
    public GroupBox(String titleString, Node content)
    {
        Label title = new Label(" " + titleString + " ");
        title.setTranslateY(-12);
        title.setBackground(Background.fill(Color.WHITE));
        title.setFont(Font.font(title.getFont().getFamily(),FontWeight.BOLD,title.getFont().getSize()));
        StackPane.setAlignment(title, Pos.TOP_CENTER);

        StackPane contentPane = new StackPane();
        contentPane.getChildren().add(content);

        Border blackBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(2)));
        setBorder(blackBorder);
        getChildren().addAll(title, contentPane);
    }
}