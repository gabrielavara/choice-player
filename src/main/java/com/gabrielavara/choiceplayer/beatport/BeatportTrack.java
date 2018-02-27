package com.gabrielavara.choiceplayer.beatport;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
class BeatportTrack implements BeatportSearchOutput, BeatportSearchInput {
    private String trackNumber;
    private List<String> artists;
    private String title;
    private String mix;
    private List<String> genres;
    private int bpm;
}
