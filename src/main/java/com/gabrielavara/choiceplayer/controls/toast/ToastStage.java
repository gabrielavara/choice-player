package com.gabrielavara.choiceplayer.controls.toast;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ToastStage extends Stage {
    private final Location bottomRight;

    ToastStage(Pane pane) {
        initStyle(StageStyle.TRANSPARENT);

        setSize(pane.getPrefWidth(), pane.getPrefHeight());

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = screenBounds.getMinX() + screenBounds.getWidth() - pane.getPrefWidth() - 2;
        double y = screenBounds.getMinY() + screenBounds.getHeight() - pane.getPrefHeight() - 2;

        bottomRight = Location.at(x, y);
    }

    Location getBottomRight() {
        return bottomRight;
    }

    private void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    Location getOffScreenBounds() {
        Location loc = getBottomRight();
        return Location.at(loc.getX() + getWidth(), loc.getY());
    }

    public void setLocation(Location location) {
        setX(location.getX());
        setY(location.getY());
    }

    private SimpleDoubleProperty xLocationProperty = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            setX(newValue);
        }

        @Override
        public double get() {
            return getX();
        }
    };

    SimpleDoubleProperty xLocationProperty() {
        return xLocationProperty;
    }

    private SimpleDoubleProperty yLocationProperty = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            setY(newValue);
        }

        @Override
        public double get() {
            return getY();
        }
    };

    public SimpleDoubleProperty yLocationProperty() {
        return yLocationProperty;
    }

}
