package com.gabrielavara.choiceplayer.views;

import static java.text.MessageFormat.format;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.util.TimeFormatter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PlaylistItemView {
    @Getter
    private String indexAsString;
    @Getter
    private int index;
    @Getter
    private Mp3 mp3;
    @Getter
    private String length;

    public PlaylistItemView(int index, Mp3 mp3) {
        setIndex(index);
        this.mp3 = mp3;
        length = TimeFormatter.getFormattedLength((int) mp3.getLength() / 1000);
    }

    public String getArtist() {
        return mp3.getArtist();
    }

    public String getTitle() {
        return mp3.getTitle();
    }

    public void setIndex(int index) {
        this.index = index;
        indexAsString = format("{0}", index);
    }
}
