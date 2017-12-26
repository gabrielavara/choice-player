package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.RECYCLE_BIN;

import java.io.File;
import java.io.IOException;

import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXSnackbar;
import com.sun.jna.platform.FileUtils;
import javafx.collections.ObservableList;

public class RecycleBinFileMover extends FileMover {

    public RecycleBinFileMover(PlaylistUtil playlistUtil, ObservableList<PlaylistItemView> mp3Files, PlaylistInitializer playlistInitializer, JFXSnackbar snackBar) {
        super(playlistUtil, mp3Files, playlistInitializer, snackBar);
    }

    @Override
    protected String getTarget() {
        return RECYCLE_BIN;
    }

    @Override
    protected void moveFile(PlaylistItemView itemView) throws IOException {
        FileUtils fileUtils = FileUtils.getInstance();
        fileUtils.moveToTrash(new File[]{new File(itemView.getMp3().getFilename())});
    }
}
