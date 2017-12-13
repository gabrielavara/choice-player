package com.gabrielavara.choiceplayer.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.api.service.PlaylistLoader;
import com.gabrielavara.choiceplayer.messages.TableItemSelectedMessage;
import com.gabrielavara.choiceplayer.views.TableItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GoodFolderFileMoverTest {
    private static final String C_DRIVE = "C:\\";
    private static final String TEST_RESOURCES = "src/test/resources/mp3folder";
    private static final String TEST_FILE = "testOlder.mp3";
    private ObservableList<TableItem> mp3Files = FXCollections.observableArrayList();
    private GoodFolderFileMover goodFolderFileMover;
    private MediaPlayer mediaPlayer;

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

    @Before
    public void setup() {
        List<Mp3> files = new PlaylistLoader().load(Paths.get(TEST_RESOURCES));
        ObservableList<Mp3> mp3List = FXCollections.observableList(files);
        List<TableItem> tableItems = IntStream.range(0, files.size()).mapToObj(index -> new TableItem(index + 1, files.get(index)))
                        .collect(Collectors.toList());
        mp3Files.addAll(tableItems);
        PlaylistUtil playlistUtil = new PlaylistUtil(mp3Files);
        goodFolderFileMover = new GoodFolderFileMover(playlistUtil, mp3Files) {
            @Override
            protected String getTarget() {
                return C_DRIVE;
            }
        };
        Messenger.register(TableItemSelectedMessage.class, this::selectionChanged);
    }

    private void selectionChanged(TableItemSelectedMessage message) {
        Mp3 mp3 = message.getTableItem().getMp3();
        mp3Files.get(1).getMp3().setCurrentlyPlaying(true);
        mp3.setCurrentlyPlaying(false);
        createMediaPlayer(mp3);
    }

    @Ignore
    @Test
    public void testMoveFileToGoodFolder() throws IOException {
        // given
        mp3Files.get(0).getMp3().setCurrentlyPlaying(true);
        createMediaPlayer(mp3Files.get(0).getMp3());

        // when
        goodFolderFileMover.moveFile();

        // then
        assertEquals(3, mp3Files.size());
        assertTrue(mp3Files.get(0).getMp3().isCurrentlyPlaying());

        // reset
        Files.move(Paths.get(C_DRIVE + TEST_FILE), Paths.get(TEST_RESOURCES + TEST_FILE), REPLACE_EXISTING);
    }

    private void createMediaPlayer(Mp3 mp3) {
        Optional<String> s = MediaUrl.create(mp3);
        s.ifPresent(url -> mediaPlayer = new MediaPlayer(new Media(url)));
    }
}