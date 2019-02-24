package com.gabrielavara.choiceplayer.playlist;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.messages.SelectionChangedMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.gabrielavara.choiceplayer.util.Opinion;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Optional;

public class PlaylistSelectionChangedListener implements ChangeListener<PlaylistItemView> {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Opinion> opinion = Optional.empty();
    private boolean opinionSet;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void setOpinion(Optional<Opinion> opinion) {
        this.opinion = opinion;
        opinionSet = true;
    }

    @Override
    public void changed(ObservableValue<? extends PlaylistItemView> observable, PlaylistItemView oldValue, PlaylistItemView newValue) {
        if (newValue == null) {
            return;
        }
        changed(oldValue == null ? null : oldValue.getMp3(), newValue.getMp3(), oldValue);
    }

    private void changed(Mp3 oldValue, Mp3 newValue, PlaylistItemView oldItemView) {
        if (!opinionSet) {
            opinion = Optional.empty();
        }
        Messenger.send(new SelectionChangedMessage(newValue, oldValue, oldItemView, true, opinion));
        opinionSet = false;
    }
}
