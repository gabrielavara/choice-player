package com.gabrielavara.choiceplayer.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.ImageUtil");

    private ImageUtil() {
    }

    public static Image getAlbumArt(Optional<byte[]> albumArtData) {
        return albumArtData.map(bytes -> new Image(new ByteArrayInputStream(bytes))).orElseGet(ImageUtil::getDefaultImage);
    }

    public static Image getDefaultImage() {
        try {
            FileInputStream inputStream = new FileInputStream("src/main/resources/images/defaultAlbumArt.jpg");
            Image image = new Image(inputStream);
            inputStream.close();
            return image;
        } catch (IOException e) {
            log.warn("Could not load default album art");
        }
        return null;
    }
}
