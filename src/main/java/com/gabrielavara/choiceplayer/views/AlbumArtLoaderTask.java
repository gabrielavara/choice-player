package com.gabrielavara.choiceplayer.views;

import java.util.Optional;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.util.ImageUtil;
import com.gabrielavara.choiceplayer.util.PlaylistUtil;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.Getter;

public class AlbumArtLoaderTask extends Task<Image> {
    @Getter
    private Mp3 mp3;

    AlbumArtLoaderTask(Mp3 mp3) {
        this.mp3 = mp3;
    }

    @Override
    protected Image call() {
        Optional<byte[]> albumArtData = PlaylistUtil.getAlbumArt(mp3);
        return ImageUtil.getAlbumArt(albumArtData);
    }
}
