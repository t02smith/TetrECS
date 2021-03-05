package uk.ac.soton.comp1206.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class GamePane extends StackPane {
    private static final Logger logger = LogManager.getLogger(GamePane.class);

    private final int width;
    private final int height;

    private double scalar;
    private boolean autoScale = true;

    public GamePane(int width, int height) {
        super();
        this.width = width;
        this.height = height;

        this.getStyleClass().add("game-pane");
        this.setAlignment(Pos.TOP_LEFT);
    }

    protected void setScalar(double scalar) {
        this.scalar = scalar;
    }

    public void layoutChildren() {
        super.layoutChildren();

        logger.info("asdahdsa");

        if (!autoScale) return;

        var sfHeight = this.getHeight() / this.height;
        var sfWidth = this.getWidth() / this.width;

        setScalar(
            (sfHeight > sfWidth) ? sfWidth: sfHeight
        );

        var scale = new Scale(this.scalar, this.scalar);

        //Get the parent width and height
        var parentWidth = this.getWidth();
        var parentHeight = this.getHeight();

        //Get the padding needed on the top and left
        var paddingLeft = (parentWidth - (this.width * scalar)) / 2.0;
        var paddingTop = (parentHeight - (this.height * scalar)) / 2.0;

        //Perform the transformation
        Translate translate = new Translate(paddingLeft, paddingTop);
        scale.setPivotX(0);
        scale.setPivotY(0);
        getTransforms().setAll(translate, scale);
    }

}
