package com.gabrielavara.choiceplayer.messages;

import lombok.Getter;

@Getter
public class SettingsClosedMessage {
    private boolean folderChanged;

    public SettingsClosedMessage(boolean folderChanged) {
        this.folderChanged = folderChanged;
    }
}
