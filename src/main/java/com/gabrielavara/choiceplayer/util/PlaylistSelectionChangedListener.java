package com.gabrielavara.choiceplayer.util;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaylistSelectionChangedListener implements ChangeListener<PlaylistItemView> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.PlaylistSelectionChangedListener");

    @Override
    public void changed(ObservableValue<? extends PlaylistItemView> observable, PlaylistItemView oldValue, PlaylistItemView newValue) {
        changed(oldValue == null ? null : oldValue.getMp3(), newValue == null ? null : newValue.getMp3());
    }

    private void changed(Mp3 oldValue, Mp3 newValue) {
        if (newValue == null) {
            return;
        }
        log.info("Playlist selection changed from: {}, to {}", oldValue, newValue);
        newValue.setCurrentlyPlaying(true);
        if (oldValue != null) {
            oldValue.setCurrentlyPlaying(false);
        }

        Messenger.send(new SelectionChangedMessage(newValue, oldValue));
    }
}
