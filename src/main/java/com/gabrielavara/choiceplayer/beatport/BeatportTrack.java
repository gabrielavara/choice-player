package com.gabrielavara.choiceplayer.beatport;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class BeatportTrack implements BeatportSearchOutput, BeatportSearchInput {
    private String trackNumber;
    private List<String> artists;
    private String title;
    private String mix;
}
