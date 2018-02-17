package com.gabrielavara.choiceplayer.playlist;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class PlaylistSelectionChangedListener implements ChangeListener<PlaylistItemView> {
    @Override
    public void changed(ObservableValue<? extends PlaylistItemView> observable, PlaylistItemView oldValue, PlaylistItemView newValue) {
        changed(oldValue == null ? null : oldValue.getMp3(), newValue == null ? null : newValue.getMp3());
    }

    private void changed(Mp3 oldValue, Mp3 newValue) {
        if (newValue == null) {
            return;
        }
        Messenger.send(new SelectionChangedMessage(newValue, oldValue, true));
    }
}
