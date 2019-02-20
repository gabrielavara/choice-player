package com.gabrielavara.choiceplayer.playlist;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.messages.PlaylistLoadedMessage;
import com.gabrielavara.choiceplayer.messages.SelectItemInNewPlaylistMessage;
import com.gabrielavara.choiceplayer.messages.SnackBarMessage;
import com.gabrielavara.choiceplayer.messenger.Messenger;
import com.gabrielavara.choiceplayer.views.PlaylistCell;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.gabrielavara.choiceplayer.Constants.LOAD_FILES_FROM_DISK;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItemState.SELECTED;
import static java.util.stream.Collectors.toList;

public class Playlist {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.playlist.Playlist");

    private ObservableList<PlaylistItemView> playlistItemViews;
    private final PlaylistAnimator playlistAnimator;

    public Playlist(JFXListView<PlaylistItemView> playlistView, ObservableList<PlaylistItemView> playlistItemViews, PlaylistAnimator playlistAnimator) {
        this.playlistItemViews = playlistItemViews;
        this.playlistAnimator = playlistAnimator;
        playlistView.setItems(playlistItemViews);
        Messenger.register(SelectItemInNewPlaylistMessage.class, this::selectItemInNewPlaylist);
    }

    public Optional<PlaylistCell> getCell(PlaylistItemView playlistItemView) {
        return playlistAnimator.getCell(playlistItemView);
    }

    public List<PlaylistCell> getCellsAfter(PlaylistItemView item) {
        return playlistAnimator.getCellsAfter(item);
    }

    public void changeTheme() {
        playlistAnimator.changeTheme();
    }

    public void load() {
        load(true);
    }

    public void reload() {
        playlistAnimator.animateOutItems(ev -> load(), Optional.empty());
    }

    public void reloadWithoutCache() {
        Optional<PlaylistItemView> selected = playlistItemViews.stream().filter(v -> v.getMp3().isCurrentlyPlaying()).findFirst();
        playlistAnimator.animateOutItems(ev -> load(false), selected);
    }

    private void load(boolean withCache) {
        List<PlaylistItemView> cachedItems = new ArrayList<>();
        Optional<PlaylistItemView> selected = playlistItemViews.stream().filter(v -> v.getMp3().isCurrentlyPlaying()).findFirst();
        playlistItemViews.clear();

        if (withCache) {
            loadCache(cachedItems);
        }

        Task<List<PlaylistItemView>> playListLoaderTask = createPlaylistLoaderTask(cachedItems, selected);
        new Thread(playListLoaderTask).start();
    }

    private void loadCache(List<PlaylistItemView> cachedItems) {
        log.info("Load cache");
        cachedItems.addAll(PlaylistCache.load());
        if (!cachedItems.isEmpty()) {
            log.info("Cached items found");
            playlistItemViews.addAll(cachedItems);
            playlistAnimator.showItems(Optional.empty(), true);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Task<List<PlaylistItemView>> createPlaylistLoaderTask(List<PlaylistItemView> cachedItems, Optional<PlaylistItemView> selected) {
        Messenger.send(new SnackBarMessage(LOAD_FILES_FROM_DISK));
        Task<List<PlaylistItemView>> playListLoaderTask = new Task<List<PlaylistItemView>>() {
            @Override
            protected List<PlaylistItemView> call() {
                Path folder = getFolder();
                log.info("Load playlist from {}", folder);
                List<Mp3> files = createPlaylistLoader().load(folder);
                return IntStream.range(0, files.size()).mapToObj(index -> new PlaylistItemView(index + 1, files.get(index))).collect(toList());
            }
        };

        playListLoaderTask.setOnSucceeded(e -> {
            List<PlaylistItemView> items = playListLoaderTask.getValue();
            if (cachedItems.isEmpty() && items.isEmpty()) {
                log.info("Cache empty, new items empty");
                playlistAnimator.showItems(Optional.empty(), false);
            }

            if (!cachedItems.equals(items)) {
                log.info("Loaded playlist not equals cached playlist");
                reloadItems(items, selected);
            } else if (!items.isEmpty()) {
                log.info("Loaded playlist equals cached playlist");
                Messenger.send(new PlaylistLoadedMessage());
            }
        });
        return playListLoaderTask;
    }

    PlaylistLoader createPlaylistLoader() {
        return new PlaylistLoader();
    }

    Path getFolder() {
        return Paths.get(ChoicePlayerApplication.getSettings().getFolder());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void reloadItems(List<PlaylistItemView> items, Optional<PlaylistItemView> selected) {
        if (playlistItemViews.isEmpty()) {
            log.info("Empty playlist, show new items");
            playlistItemViews.addAll(items);
            playlistAnimator.showItems(selected, false);
        } else {
            log.info("Animate out items, show new items");
            playlistAnimator.animateItems(OUT, ev -> {
                playlistItemViews.clear();
                playlistItemViews.addAll(items);
                playlistAnimator.showItems(selected, false);
            }, Optional.empty(), false);
        }
    }

    private void selectItemInNewPlaylist(SelectItemInNewPlaylistMessage m) {
        Optional<PlaylistItemView> newSelected = playlistItemViews.stream().filter(item -> item.getMp3().equals(m.getSelected().getMp3())).findFirst();
        newSelected.ifPresent(s -> {
            Optional<PlaylistCell> cell = playlistAnimator.getCell(s);
            cell.ifPresent(c -> c.getPlaylistItem().animateToState(SELECTED));
            s.getMp3().setCurrentlyPlaying(true);
        });
    }
}
