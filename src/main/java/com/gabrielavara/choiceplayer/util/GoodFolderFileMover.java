package com.gabrielavara.choiceplayer.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.views.TableItem;
import javafx.collections.ObservableList;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.hamcrest.CoreMatchers;

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

    void delete(final String url) {
        Path path = Paths.get(url);
        try {
            Awaitility.with().pollInterval(500, MILLISECONDS).await().atMost(5, SECONDS)
                    .until(deleteFile(path), CoreMatchers.equalTo(false));
        } catch (ConditionTimeoutException e) {
            log.debug("Could not delete :( {}", path);
        }
    }

    private Callable<Boolean> deleteFile(Path path) {
        try {
            Files.delete(path);
            log.info("File deleted {}", path);
        } catch (IOException e) {
            log.debug("Could not delete {}, wait a little...", path);
        }
        return () -> path.toFile().exists();
    }
}
