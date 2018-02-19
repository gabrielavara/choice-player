package com.gabrielavara.choiceplayer.beatport;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class BeatportRelease implements BeatportSearchInput {
    private List<String> artists;
    private String album;
    private String link;
}
