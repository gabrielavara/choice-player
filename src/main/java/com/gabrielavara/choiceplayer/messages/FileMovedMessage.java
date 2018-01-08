package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.util.Opinion;

import lombok.Getter;

@Getter
public class FileMovedMessage {
    private Opinion opinion;

    public FileMovedMessage(Opinion opinion) {
        this.opinion = opinion;
    }
}
