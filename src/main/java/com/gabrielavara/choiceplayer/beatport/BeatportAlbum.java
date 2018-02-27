package com.gabrielavara.choiceplayer.beatport;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
class BeatportAlbum implements BeatportSearchOutput {
    private List<String> artists;
    private String title;
    private List<BeatportTrack> tracks = new ArrayList<>();
    private String albumArtUrl;
    private String releaseDate;
    private String label;
    private String catalog;

    public void addTrack(BeatportTrack beatportTrack) {
        tracks.add(beatportTrack);
    }
}
