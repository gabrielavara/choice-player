package com.gabrielavara.choiceplayer.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.api.service.PlaylistTestInitializer;
import com.gabrielavara.choiceplayer.messages.TableItemSelectedMessage;
import com.gabrielavara.choiceplayer.views.TableItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class GoodFolderFileMoverTest extends PlaylistTestInitializer {
    private static final String C_DRIVE = "C:\\";
    private static final String TEST_RESOURCES = "src/test/resources/mp3folder";
    private static final String TEST_FILE = "testOlder.mp3";
    private static final String TEMP = "temp";
    private ObservableList<TableItem> mp3Files = FXCollections.observableArrayList();
    private GoodFolderFileMover goodFolderFileMover;
    private MediaPlayer mediaPlayer;
    private boolean didTempExist;
    private File temp = new File(C_DRIVE + TEMP);

    @BeforeClass
    public static void initToolkit() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });

        if (!latch.await(5L, SECONDS)) {
            throw new ExceptionInInitializerError();
        }
    }

    @Override
    @Before
    public void setup() throws IOException {
        super.setup();
        List<Mp3> files = new PlaylistLoader().load(Paths.get(TEST_RESOURCES));
        ObservableList<Mp3> mp3List = FXCollections.observableList(files);
        List<TableItem> tableItems = IntStream.range(0, files.size()).mapToObj(index -> new TableItem(index + 1, files.get(index)))
                .collect(Collectors.toList());
        mp3Files.addAll(tableItems);
        PlaylistUtil playlistUtil = new PlaylistUtil(mp3Files);

        didTempExist = createTempIfNotExists();

        goodFolderFileMover = new GoodFolderFileMover(playlistUtil, GoodFolderFileMoverTest.this.mp3Files) {
            @Override
            protected String getTarget() {
                return C_DRIVE + TEMP + "\\";
            }
        };
        Messenger.register(TableItemSelectedMessage.class, this::selectionChanged);
    }

    private boolean createTempIfNotExists() {
        if (!temp.exists()) {
            assertTrue(temp.mkdir());
            return false;
        } else {
            return true;
        }
    }

    private void selectionChanged(TableItemSelectedMessage message) {
        Mp3 mp3 = message.getTableItem().getMp3();
        mp3Files.get(0).getMp3().setCurrentlyPlaying(false);
        mp3.setCurrentlyPlaying(true);
        createMediaPlayer(mp3);
    }

    @Ignore
    @Test
    public void testMoveFileToGoodFolder() throws IOException {
        Path tempPath = Paths.get(temp + "\\" + TEST_FILE);
        Path resourcesPath = Paths.get(TEST_RESOURCES + "\\" + TEST_FILE);

        try {
            // given
            mp3Files.get(0).getMp3().setCurrentlyPlaying(true);
            createMediaPlayer(mp3Files.get(0).getMp3());

            // when
            goodFolderFileMover.moveFile();

            // then
            assertEquals(3, mp3Files.size());
            assertFalse(Files.exists(resourcesPath));
            assertTrue(Files.exists(tempPath));
            assertTrue(mp3Files.get(0).getMp3().isCurrentlyPlaying());
        } finally {
            // reset
            if (mp3Files.size() == 4) {
                Files.delete(tempPath);
            } else {
                Files.copy(tempPath, resourcesPath, REPLACE_EXISTING);
                Files.deleteIfExists(tempPath);
            }
            if (!didTempExist) {
                assertTrue(temp.delete());
            }
        }
    }

    private void createMediaPlayer(Mp3 mp3) {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            waitForDispose();
        }
        Optional<String> s = MediaUrl.create(mp3);
        s.ifPresent(url -> mediaPlayer = new MediaPlayer(new Media(url)));
    }

    private void waitForDispose() {
        while (!MediaPlayer.Status.DISPOSED.equals(mediaPlayer.statusProperty().get())) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Nothing to do
            }
        }
    }
}