package main.java.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.java.BasicNetworking.Router;
import main.java.NetworkConfigurator.GUIConnector;
import org.jetbrains.annotations.NotNull;

public class StartConfigTab extends BasicTab
{

    protected ProgressBar[] progressBars;

    public StartConfigTab(Router[] routers, Tab[] tabs)
    {
        super("Start");
        constructPane(routers, tabs);
    }

    private void constructPane(Router @NotNull [] routers, Tab[] tabs)
    {
        HBox box = new HBox();
        box.setSpacing(20);


        GridPane gridPane = new GridPane();
        gridPane.setVgap(50);
        gridPane.setHgap(50);
        gridPane.setPadding(new Insets(20, 0, 0, 20));


        progressBars = new ProgressBar[routers.length];
        for (int i = 0; i < routers.length; i++)
        {
            gridPane.add(new Label(routers[i].hostname()), 0, i);
            progressBars[i] = new ProgressBar(0);
            progressBars[i].setPrefSize(250, 20);
            gridPane.add(progressBars[i], 1, i);
        }
        Button start = new Button("Start");
        Button verify = new Button("Verify");

        ListView<String> problemsList = new ListView<>();
        problemsList.setPrefSize(1000, 200);
        problemsList.setCellFactory(list -> new RedListViewCell());

        gridPane.add(start, 0, routers.length + 1);
        gridPane.add(verify, 1, routers.length + 1);

        box.getChildren().add(gridPane);
        box.getChildren().add(problemsList);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);

        start.setOnAction(event ->
        {
            problemsList.getItems().clear();
            GUIConnector guiConnector = new GUIConnector(tabs, problemsList, routers);
            guiConnector.startAllConfiguration(progressBars, start);
        });

        verify.setOnAction(event ->
        {
            problemsList.getItems().clear();
            GUIConnector guiConnector = new GUIConnector(tabs, problemsList, routers);
            guiConnector.verifyAllInput();
        });

        anchorPane.getChildren().add(box);
    }

    public static class RedListViewCell extends ListCell<String>
    {
        @Override
        protected void updateItem(String item, boolean empty)
        {
            super.updateItem(item, empty);
            setText(item);
            setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            if (item != null && !item.equals("No pre-parse problems were found!") && !item.equals("Configuration finished, you may check the results"))
            {
                setTextFill(Color.RED);
            }
            else
            {
                setTextFill(Color.GREEN);
            }
        }
    }
}
