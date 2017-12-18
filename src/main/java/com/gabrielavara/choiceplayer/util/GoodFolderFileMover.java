package com.gabrielavara.choiceplayer.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.views.TableItem;

import javafx.collections.ObservableList;

public class GoodFolderFileMover extends FileMover {

    public GoodFolderFileMover(PlaylistUtil playlistUtil, ObservableList<TableItem> mp3Files) {
        super(playlistUtil, mp3Files);
    }

    @Override
    protected String getTarget() {
        return ChoicePlayerApplication.getSettings().getFolderToMove();
    }

    @Override
    protected void moveFile(TableItem tableItem) throws IOException {
        Path from = Paths.get(tableItem.getMp3().getFilename());
        String folderToMove = getTarget();
        String fileName = from.getFileName().toString();
        Path to = Paths.get(folderToMove, fileName);
        Files.copy(from, to, REPLACE_EXISTING);
        delete(from.toString());
    }
}
