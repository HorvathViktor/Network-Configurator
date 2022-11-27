package main.java.GUI.Topology;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.GUI.BasicTab;

import java.awt.*;
import java.io.File;


public class TopologyTab extends BasicTab
{
    private Desktop desktop = Desktop.getDesktop();
    private Image image;

    public TopologyTab(Image image, Stage stage)
    {
        super("Topology");
        constructPane(image, stage);
    }

    private void constructPane(Image image, Stage stage)
    {
        VBox box = new VBox();
        this.image = image;
        ImageView imageView = new ImageView();
        imageView.setImage(this.image);
        box.getChildren().add(imageView);

        Button add = new Button("Add");
        box.getChildren().add(add);
        anchorPane.getChildren().add(box);

        add.setOnAction(event ->
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null)
            {
                this.image = new Image(file.getAbsolutePath());
                imageView.setImage(this.image);
            }
        });
    }

}
