package uk.ac.soton.comp1206.Components.multiplayer;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Holds the online side panel
 *  allows users to toggle between:
 *  -> Other player's grids
 *  -> The game chat
 *  -> The current leaderboard
 * 
 * @author tcs1g20
 */
public class OnlinePanel extends BorderPane {
    //The panel components being displayed
    private StackPane panelDisplay;

    //The current panel being displayed
    private Node top;
    private int panelIndex = 0;

    //Bar used to navigate the panels
    private HBox navigation;
    
    //The list of panels
    private ArrayList<Node> panels = new ArrayList<>();

    /**
     * Constructor to create a new online panel with a set of components
     * @param panels The collection of panels
     */
    public OnlinePanel(Node... panels) {
        this.panels.addAll(Arrays.asList(panels));
        this.panels.forEach(panel -> panel.setOpacity(0));
        this.build();
    }

    /**
     * Builds the component
     */
    public void build() {
        //ON SCREEN NAVIGATION//
        this.navigation = new HBox(10);

        var emptyL = new Region();
        HBox.setHgrow(emptyL, Priority.ALWAYS);
        this.navigation.getChildren().add(emptyL);

        var center = new HBox(emptyL);

        var emptyR = new Region();
        HBox.setHgrow(emptyR, Priority.ALWAYS);

        //PANELS//
        this.panelDisplay = new StackPane();
        this.panels.forEach(panel -> {
            this.panelDisplay.getChildren().add(panel);

            var panelIcon = new Label("\u2586");
            panelIcon.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.4); -fx-font-size: 24px;");
            panelIcon.setAlignment(Pos.CENTER);

            this.navigation.getChildren().add(panelIcon);
        });

        center.getChildren().addAll(this.navigation, emptyR);

        this.toggleOnlinePanel();

        this.setTop(center);
        this.setCenter(this.panelDisplay);
    }

    /**
     * Toggles which panel is currently visible
     */
    public void toggleOnlinePanel() {
        if (this.top != null) {
            //Make the old panel invisible and add it to the back of the stackpane
            var fadeOut = new FadeTransition(Duration.millis(200), this.top);
            fadeOut.setByValue(-1);
            fadeOut.play();

            this.panelDisplay.getChildren().remove(top);
            this.panelDisplay.getChildren().add(0, top);
        }

        //Find the new top and make it visible
        //Request focus to stop typing on the chatpane

        var newTop = this.panelDisplay.getChildren().get(
            this.panelDisplay.getChildren().size()-1
        );

        var fadeIn = new FadeTransition(Duration.millis(200), newTop);
        fadeIn.setByValue(1);
        fadeIn.play();

        newTop.requestFocus();

        this.navigation.getChildren().get(this.panelIndex).setStyle(
            "-fx-text-fill: rgba(255, 255, 255, 0.4); -fx-font-size: 24px;"
        );

        this.panelIndex = (this.panelIndex+1) % this.panels.size();
        this.navigation.getChildren().get(this.panelIndex).setStyle(
            "-fx-text-fill: rgba(255, 255, 255, 1); -fx-font-size: 24px;"
        );

        this.top = newTop;
    }


}
