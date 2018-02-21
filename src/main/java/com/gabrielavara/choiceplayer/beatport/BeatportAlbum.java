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

    public void addTrack(BeatportTrack beatportTrack) {
        tracks.add(beatportTrack);
    }
}
