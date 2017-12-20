package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.DISPOSE_MAX_WAIT_S;
import static com.gabrielavara.choiceplayer.Constants.DISPOSE_WAIT_MS;
import static com.gabrielavara.choiceplayer.Constants.FILE_MOVER_MAX_WAIT_S;
import static com.gabrielavara.choiceplayer.Constants.FILE_MOVER_WAIT_MS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javafx.scene.media.MediaPlayer.Status.DISPOSED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class GoodFolderFileMoverTest extends PlaylistTestInitializer {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.GoodFolderFileMoverTest");
    private static final String C_DRIVE = "C:\\";
    private static final String TEST_RESOURCES = "src/test/resources/mp3";
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
        assertFalse(files.get(0).getFilename().contains("Album"));
        assertTrue(files.get(0).getFilename().contains("Older"));

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
            Awaitility.with().pollInterval(FILE_MOVER_WAIT_MS, MILLISECONDS).await().atMost(FILE_MOVER_MAX_WAIT_S, SECONDS).until(fileDeleted(resourcesPath));

            // then
            assertEquals(3, mp3Files.size());
            assertFalse(Files.exists(resourcesPath));
            assertTrue(Files.exists(tempPath));
            assertTrue(mp3Files.get(0).getMp3().isCurrentlyPlaying());
        } finally {
            // tear down
            log.debug("Test tear down");
            tearDown(tempPath, resourcesPath);
        }
    }

    private Callable<Boolean> fileDeleted(Path path) {
        return () -> !path.toFile().exists();
    }

    private void createMediaPlayer(Mp3 mp3) {
        disposeMediaPlayer();
        loadBeep();
        disposeMediaPlayer();
        loadMediaPlayer(mp3);
    }

    private void loadBeep() {
        Optional<String> mediaUrl = MediaUrl.create(Paths.get("src/main/resources/mp3/beep.mp3"));
        mediaUrl.ifPresent(url -> mediaPlayer = new MediaPlayer(new Media(url)));
    }

    private void loadMediaPlayer(Mp3 mp3) {
        Optional<String> mediaUrl = MediaUrl.create(mp3);
        mediaUrl.ifPresent(url -> mediaPlayer = new MediaPlayer(new Media(url)));
    }

    private void disposeMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            waitForDispose();
        }
    }

    private void waitForDispose() {
        try {
            Awaitility.with().pollInterval(DISPOSE_WAIT_MS, MILLISECONDS).await().atMost(DISPOSE_MAX_WAIT_S, SECONDS)
                            .until(() -> mediaPlayer.statusProperty().get(), equalTo(DISPOSED));
        } catch (ConditionTimeoutException e) {
            log.debug("Media player not disposed :( {}");
        }
    }

    private void tearDown(Path tempPath, Path resourcesPath) throws IOException {
        if (mp3Files.size() == 4) {
            goodFolderFileMover.delete(temp + "\\" + TEST_FILE);
        } else {
            if (Files.notExists(resourcesPath)) {
                Files.copy(tempPath, resourcesPath);
            }
            goodFolderFileMover.delete(temp + "\\" + TEST_FILE);
        }
        if (!didTempExist) {
            assertTrue(temp.delete());
        }
    }
}