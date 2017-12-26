package com.gabrielavara.choiceplayer.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.collections.ObservableList;

public class GoodFolderFileMover extends FileMover {

    public GoodFolderFileMover(PlaylistUtil playlistUtil, ObservableList<PlaylistItemView> mp3Files, PlaylistInitializer playlistInitializer) {
        super(playlistUtil, mp3Files, playlistInitializer);
    }

    @Override
    protected String getTarget() {
        return ChoicePlayerApplication.getSettings().getFolderToMove();
    }

    @Override
    protected void moveFile(PlaylistItemView itemView) throws IOException {
        Path from = Paths.get(itemView.getMp3().getFilename());
        String folderToMove = getTarget();
        String fileName = from.getFileName().toString();
        Path to = Paths.get(folderToMove, fileName);
        Files.move(from, to, REPLACE_EXISTING);
    }
}
