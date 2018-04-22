package com.gabrielavara.choiceplayer.controls;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
final class Location {
    static Location at(double x, double y) {
        return new Location(x, y);
    }

    private final double x;
    private final double y;
}
