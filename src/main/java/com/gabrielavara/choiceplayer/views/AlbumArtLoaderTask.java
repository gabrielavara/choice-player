package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.ALBUM_ART_SIZE;

import java.util.Optional;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.util.ImageUtil;

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
        Optional<byte[]> albumArtData = mp3.getAlbumArt();
        return ImageUtil.getAlbumArt(albumArtData, ALBUM_ART_SIZE);
    }
}
