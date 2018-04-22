package com.gabrielavara.choiceplayer.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SettingsClosedMessage {
    private final boolean folderChanged;
}
