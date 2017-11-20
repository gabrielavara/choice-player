package com.gabrielavara.choiceplayer.views;

import com.gabrielavara.choiceplayer.api.service.Mp3;
import com.gabrielavara.choiceplayer.util.TimeFormatter;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter
public class TableItem extends RecursiveTreeObject<TableItem> {
    private StringProperty index;
    private StringProperty artist;
    private StringProperty track;
    private StringProperty length;
    private Mp3 mp3;

    public TableItem(int index, Mp3 mp3) {
        this.mp3 = mp3;
        this.index = new SimpleStringProperty(String.valueOf(index));
        this.artist = new SimpleStringProperty(mp3.getArtist());
        this.track = new SimpleStringProperty(mp3.getTitle());
        this.length = new SimpleStringProperty(TimeFormatter.getFormattedLength((int) mp3.getLength() / 1000));
    }
}
