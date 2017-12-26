package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.FILE_MOVER_MAX_WAIT_S;
import static com.gabrielavara.choiceplayer.Constants.FILE_MOVER_WAIT_MS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.collections.ObservableList;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

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
        Files.copy(from, to, REPLACE_EXISTING);
        delete(from.toString());
    }

    private void delete(final String url) {
        Path path = Paths.get(url);
        try {
            Awaitility.with().pollInterval(FILE_MOVER_WAIT_MS, MILLISECONDS).await().atMost(FILE_MOVER_MAX_WAIT_S, SECONDS).until(fileDeleted(path));
            log.info("File deleted {}", path);
        } catch (ConditionTimeoutException e) {
            log.error("Could not delete :( {}", path);
        }
    }

    private Callable<Boolean> fileDeleted(Path path) {
        try {
            Files.delete(path);
            log.info("File could be deleted {}", path);
        } catch (IOException e) {
            log.error("Could not delete {}, wait a little...", path);
        }
        return () -> !path.toFile().exists();
    }
}
