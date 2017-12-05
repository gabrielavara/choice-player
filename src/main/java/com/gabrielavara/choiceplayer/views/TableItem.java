package com.gabrielavara.choiceplayer.views;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.util.TimeFormatter;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter
public class TableItem {
    private SimpleIntegerProperty index;
    private StringProperty artist;
    private StringProperty title;
    private StringProperty length;
    private Mp3 mp3;

    public TableItem(int index, Mp3 mp3) {
        this.mp3 = mp3;
        this.index = new SimpleIntegerProperty(index);
        artist = new SimpleStringProperty(mp3.getArtist());
        title = new SimpleStringProperty(mp3.getTitle());
        length = new SimpleStringProperty(TimeFormatter.getFormattedLength((int) mp3.getLength() / 1000));
    }
}
