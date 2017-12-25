package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.DEFAULT_ALBUM_ART;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.util.ImageUtil");
    private static Image defaultImage;

    private ImageUtil() {
    }

    public static Image getAlbumArt(Optional<byte[]> albumArtData) {
        return albumArtData.map(bytes -> new Image(new ByteArrayInputStream(bytes))).orElseGet(ImageUtil::getDefaultImage);
    }

    public static Image getGrayScaleAlbumArt(Optional<byte[]> albumArtData) {
        return albumArtData.map(bytes -> {
            Image image = new Image(new ByteArrayInputStream(bytes));
            return getTransformedImage(image, ImageUtil::grayScaleColorTransformer);
        }).orElseGet(() -> getTransformedImage(getDefaultImage(), ImageUtil::grayScaleColorTransformer));
    }

    public static Image getDefaultImage() {
        if (defaultImage == null) {
            try {
                FileInputStream inputStream = new FileInputStream(DEFAULT_ALBUM_ART);
                defaultImage = new Image(inputStream);
                inputStream.close();
            } catch (IOException e) {
                log.error("Could not load default album art");
            }
        }
        return defaultImage;
    }

    private static Image getTransformedImage(Image original, ColorTransformer colorTransformer) {
        WritableImage result = new WritableImage((int) original.getWidth(), (int) original.getHeight());
        PixelWriter pixelWriter = result.getPixelWriter();

        PixelReader pixelReader = original.getPixelReader();
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                pixelWriter.setColor(x, y, colorTransformer.transformColor(color));
            }
        }
        return result;
    }

    private static Color grayScaleColorTransformer(Color color) {
        return color.grayscale();
    }

    private interface ColorTransformer {
        Color transformColor(Color color);
    }
}
