package com.gabrielavara.choiceplayer.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SnackBarMessage {
    private final String resourceBundleMessageKey;
    private final Object[] objects;

    public SnackBarMessage(String resourceBundleMessageKey) {
        this.resourceBundleMessageKey = resourceBundleMessageKey;
        objects = null;
    }
}
