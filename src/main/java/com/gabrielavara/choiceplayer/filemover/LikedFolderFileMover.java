package com.gabrielavara.choiceplayer.filemover;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.playlist.Playlist;
import com.gabrielavara.choiceplayer.util.Opinion;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import javafx.collections.ObservableList;
import org.awaitility.Awaitility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static com.gabrielavara.choiceplayer.Constants.FILE_MOVER_MAX_WAIT_MS;
import static com.gabrielavara.choiceplayer.Constants.FILE_MOVER_WAIT_MS;
import static com.gabrielavara.choiceplayer.util.Opinion.LIKE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class LikedFolderFileMover extends FileMover {

    public LikedFolderFileMover(ObservableList<PlaylistItemView> mp3Files, Playlist playlist) {
        super(mp3Files, playlist);
    }

    @Override
    protected String getTarget() {
        return ChoicePlayerApplication.getSettings().getLikedFolder();
    }

    @Override
    protected Opinion getOpinion() {
        return LIKE;
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
        Awaitility.with().pollInterval(FILE_MOVER_WAIT_MS, MILLISECONDS).await()
                .atMost(FILE_MOVER_MAX_WAIT_MS, MILLISECONDS).until(fileDeleted(path));
        log.info("File deleted {}", path);
    }

    private Callable<Boolean> fileDeleted(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Could not delete {}, wait a little...", path);
        }
        return () -> !path.toFile().exists();
    }
}
