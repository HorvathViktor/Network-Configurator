package main.java.GUI.Component;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class OnOffSwitch extends StackPane
{
    private final Rectangle back = new Rectangle(30, 10, Color.RED);
    private final Button button = new Button();
    private final String buttonStyleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #f95d5d;";
    private final String buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #00893d;";
    private boolean isActive;

    public OnOffSwitch()
    {
        init();
        EventHandler<Event> click = e ->
        {
            if (isActive)
            {
                button.setStyle(buttonStyleOff);
                back.setFill(Color.valueOf("#fcacac"));
                setAlignment(button, Pos.CENTER_LEFT);
                isActive = false;
            }
            else
            {
                button.setStyle(buttonStyleOn);
                back.setFill(Color.valueOf("#80C49E"));
                setAlignment(button, Pos.CENTER_RIGHT);
                isActive = true;
            }
        };

        button.setFocusTraversable(false);
        setOnMouseClicked(click);
        button.setOnMouseClicked(click);
    }

    private void init()
    {
        getChildren().addAll(back, button);
        setMinSize(30, 15);
        back.maxWidth(30);
        back.minWidth(30);
        back.maxHeight(10);
        back.minHeight(10);
        back.setArcHeight(back.getHeight());
        back.setArcWidth(back.getHeight());
        back.setFill(Color.valueOf("#fcacac"));
        double r = 2.0;
        button.setShape(new Circle(r));
        setAlignment(button, Pos.CENTER_LEFT);
        button.setMaxSize(15, 15);
        button.setMinSize(15, 15);
        button.setStyle(buttonStyleOff);
    }

    public boolean isActive()
    {
        return isActive;
    }

    public final void setOnAction(EventHandler<ActionEvent> event) {
        this.button.setOnAction(event);
    }


}
