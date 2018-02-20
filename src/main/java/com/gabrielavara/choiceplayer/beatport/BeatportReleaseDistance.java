package com.gabrielavara.choiceplayer.beatport;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BeatportReleaseDistance {
    private int albumDistance;
    private int artistDistance;

    public int getDistanceSum() {
        return albumDistance + artistDistance;
    }
}
