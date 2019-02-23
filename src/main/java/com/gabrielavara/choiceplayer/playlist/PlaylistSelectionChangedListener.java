package com.gabrielavara.choiceplayer.playlist;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.gabrielavara.choiceplayer.util.Opinion;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.Setter;

import java.util.Optional;

public class PlaylistSelectionChangedListener implements ChangeListener<PlaylistItemView> {
    @Setter
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Opinion> opinion = Optional.empty();

    @Override
    public void changed(ObservableValue<? extends PlaylistItemView> observable, PlaylistItemView oldValue, PlaylistItemView newValue) {
        if (newValue == null) {
            return;
        }
        changed(oldValue == null ? null : oldValue.getMp3(), newValue.getMp3(), oldValue);
    }

    private void changed(Mp3 oldValue, Mp3 newValue, PlaylistItemView oldItemView) {
        Messenger.send(new SelectionChangedMessage(newValue, oldValue, oldItemView, true, opinion));
    }
}
