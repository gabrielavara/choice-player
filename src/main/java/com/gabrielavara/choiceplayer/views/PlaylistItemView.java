package com.gabrielavara.choiceplayer.views;

import static java.text.MessageFormat.format;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.util.TimeFormatter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(exclude = {"indexAsString", "index"})
@NoArgsConstructor
public class PlaylistItemView {
    @Getter
    @JsonIgnore
    private String indexAsString;
    @Getter
    private Integer index;
    @Getter
    private Mp3 mp3;
    @Getter
    private String length;

    public PlaylistItemView(int index, Mp3 mp3) {
        setIndex(index);
        this.mp3 = mp3;
        length = TimeFormatter.getFormattedLength((int) mp3.getLength() / 1000);
    }

    @JsonIgnore
    public String getArtist() {
        return mp3.getArtist();
    }

    @JsonIgnore
    public String getTitle() {
        return mp3.getTitle();
    }

    public void setIndex(int index) {
        this.index = index;
        indexAsString = format("{0}", index);
    }
}

