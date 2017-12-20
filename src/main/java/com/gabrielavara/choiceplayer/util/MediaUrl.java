package com.gabrielavara.choiceplayer.util;

import static com.gabrielavara.choiceplayer.Constants.ESCAPED_PLUS;
import static com.gabrielavara.choiceplayer.Constants.FILE;
import static com.gabrielavara.choiceplayer.Constants.PER;
import static com.gabrielavara.choiceplayer.Constants.PLUS;
import static com.gabrielavara.choiceplayer.Constants.SLASH;
import static com.gabrielavara.choiceplayer.Constants.UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.api.service.Mp3;

public class MediaUrl {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.utils.MediaUrl");

    private MediaUrl() {
    }

    public static Optional<String> create(Mp3 mp3) {
        return create(Paths.get(mp3.getFilename()));
    }

    public static Optional<String> create(Path path) {
        try {
            String mediaUrl = URLEncoder.encode(path.toAbsolutePath().toString(), UTF_8);
            return Optional.of(FILE + mediaUrl.replace(PER, SLASH).replace(PLUS, ESCAPED_PLUS));
        } catch (UnsupportedEncodingException e) {
            log.error("Could not get url: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
