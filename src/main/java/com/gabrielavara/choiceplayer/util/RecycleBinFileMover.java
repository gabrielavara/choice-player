package com.gabrielavara.choiceplayer.util;

import java.io.File;
import java.io.IOException;

import com.gabrielavara.choiceplayer.views.TableItem;
import com.sun.jna.platform.FileUtils;

import javafx.collections.ObservableList;

public class RecycleBinFileMover extends FileMover {

    public RecycleBinFileMover(PlaylistUtil playlistUtil, ObservableList<TableItem> mp3Files) {
        super(playlistUtil, mp3Files);
    }

    @Override
    protected String getTarget() {
        return "Recycle bin";
    }

    @Override
    protected void moveFile(TableItem tableItem) throws IOException {
        FileUtils fileUtils = FileUtils.getInstance();
        fileUtils.moveToTrash(new File[] {new File(tableItem.getMp3().getFilename())});
    }
}
