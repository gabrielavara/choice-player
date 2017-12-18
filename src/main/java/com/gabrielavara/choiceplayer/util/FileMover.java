package com.gabrielavara.choiceplayer.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.views.TableItem;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public abstract class FileMover {
    private static final int WAIT_MS = 500;
    private static final int MAX_WAIT_COUNT = 10;
    protected static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.utils.FileMover");

    private PlaylistUtil playlistUtil;
    private ObservableList<TableItem> mp3Files;

    FileMover(PlaylistUtil playlistUtil, ObservableList<TableItem> mp3Files) {
        this.playlistUtil = playlistUtil;
        this.mp3Files = mp3Files;
    }

    public void moveFile() {
        log.info("Move file to {}", getTarget());
        playlistUtil.getCurrentlyPlayingTableItem().ifPresent(tableItem -> {
            playlistUtil.getNextTableItem().ifPresent(playlistUtil::select);
            try {
                mp3Files.removeAll(tableItem);
                moveFile(tableItem);
                IntStream.range(0, mp3Files.size()).forEach(i -> mp3Files.get(i).setIndex(i + 1));
            } catch (IOException e) {
                mp3Files.add(tableItem.getIndex().get() - 1, tableItem);
                log.error("Could not move {} to {}", tableItem.getMp3(), getTarget());
                sortPlaylist();
            }
        });
    }

    void delete(final String url) {
        Path path = Paths.get(url);

        Task<Boolean> deleterTask = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                boolean deleted = false;
                int count = 0;

                while (!deleted && count < MAX_WAIT_COUNT) {
                    try {
                        Files.delete(path);
                        deleted = true;
                    } catch (IOException e) {
                        log.debug("Could not delete {}, wait a little...", path);
                        sleep();
                        count++;
                    }
                }

                return deleted;
            }
        };

        deleterTask.setOnSucceeded(e -> {
            Boolean couldDelete = deleterTask.getValue();
            if (!couldDelete) {
                log.info("Could not delete {}", url);
                new File(url).deleteOnExit();
            } else {
                log.info("Deleted {}", url);
            }
        });

        new Thread(deleterTask).start();

        // Awaitility.with().pollInterval(500, MILLISECONDS).await().atMost(5, SECONDS).until(deleteFile(path), CoreMatchers.equalTo(false));
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

    private void sleep() {
        try {
            Thread.sleep(WAIT_MS);
        } catch (InterruptedException ie) {
            // Nothing to do
        }
    }

    protected abstract String getTarget();

    protected abstract void moveFile(TableItem tableItem) throws IOException;

    private void sortPlaylist() {
        mp3Files.sort(Comparator.comparingInt(o -> o.getIndex().get()));
    }
}
