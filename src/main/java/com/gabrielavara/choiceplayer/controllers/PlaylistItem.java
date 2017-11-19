package com.gabrielavara.choiceplayer.controllers;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.views.PlaylistItemController;
import javafx.scene.control.ListCell;

public class PlaylistItem extends ListCell<Mp3> {
    private PlaylistItemController playlistItemController;

    PlaylistItem() {
        playlistItemController = new PlaylistItemController();
    }

    @Override
    protected void updateItem(Mp3 mp3, boolean empty) {
        super.updateItem(mp3, empty);
        if (!empty) {
            playlistItemController.setLabels(getIndex(), mp3);
            setGraphic(playlistItemController.getRootNode());
        }
    }
}
