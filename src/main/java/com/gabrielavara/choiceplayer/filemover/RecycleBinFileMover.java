package com.gabrielavara.choiceplayer.filemover;

import com.gabrielavara.choiceplayer.playlist.Playlist;
import com.gabrielavara.choiceplayer.playlist.PlaylistUtil;
import com.gabrielavara.choiceplayer.util.Opinion;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.sun.jna.platform.FileUtils;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

import static com.gabrielavara.choiceplayer.Constants.RECYCLE_BIN;
import static com.gabrielavara.choiceplayer.util.Opinion.DISLIKE;

public class RecycleBinFileMover extends FileMover {

    public RecycleBinFileMover(PlaylistUtil playlistUtil, ObservableList<PlaylistItemView> mp3Files, Playlist playlist) {
        super(playlistUtil, mp3Files, playlist);
    }

    @Override
    protected String getTarget() {
        return RECYCLE_BIN;
    }

    @Override
    protected Opinion getOpinion() {
        return DISLIKE;
    }

    @Override
    protected void moveFile(PlaylistItemView itemView) throws IOException {
        FileUtils fileUtils = FileUtils.getInstance();
        fileUtils.moveToTrash(new File[]{new File(itemView.getMp3().getFilename())});
    }
}
