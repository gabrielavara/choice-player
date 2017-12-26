package com.gabrielavara.choiceplayer.util;

import java.io.File;
import java.io.IOException;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.sun.jna.platform.FileUtils;
import javafx.collections.ObservableList;

public class RecycleBinFileMover extends FileMover {

    public RecycleBinFileMover(PlaylistUtil playlistUtil, ObservableList<PlaylistItemView> mp3Files, PlaylistInitializer playlistInitializer) {
        super(playlistUtil, mp3Files, playlistInitializer);
    }

    @Override
    protected String getTarget() {
        return "Recycle bin";
    }

    @Override
    protected void moveFile(PlaylistItemView itemView) throws IOException {
        FileUtils fileUtils = FileUtils.getInstance();
        fileUtils.moveToTrash(new File[]{new File(itemView.getMp3().getFilename())});
    }
}
