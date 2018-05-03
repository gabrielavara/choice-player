package com.gabrielavara.choiceplayer.controls;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

@Getter
public class CustomStage extends Stage {
    private final Location bottomRight;
    private final Location center;

    public CustomStage(Pane pane, StageLocation stageLocation) {
        initStyle(StageStyle.TRANSPARENT);

        setSize(pane.getPrefWidth(), pane.getPrefHeight());

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double bottomRightX = screenBounds.getMinX() + screenBounds.getWidth() - pane.getPrefWidth() - 6;
        double bottomRightY = screenBounds.getMinY() + screenBounds.getHeight() - pane.getPrefHeight() - 6;
        double centerX = (screenBounds.getMinX() + screenBounds.getWidth() - pane.getPrefWidth()) / 2;
        double centerY = (screenBounds.getMinY() + screenBounds.getHeight() - pane.getPrefHeight()) / 2;

        bottomRight = Location.at(bottomRightX, bottomRightY);
        center = Location.at(centerX, centerY);

        setAlwaysOnTop(true);
        setLocation(getLocation(stageLocation));
    }

    private Location getLocation(StageLocation stageLocation) {
        if (stageLocation == StageLocation.CENTER) {
            return center;
        } else if (stageLocation == StageLocation.BOTTOM_RIGHT) {
            return bottomRight;
        }
        return center;
    }

    private void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    private void setLocation(Location location) {
        setX(location.getX());
        setY(location.getY());
    }

    public enum StageLocation {
        CENTER, BOTTOM_RIGHT
    }
}
