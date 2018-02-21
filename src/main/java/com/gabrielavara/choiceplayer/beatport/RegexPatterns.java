package com.gabrielavara.choiceplayer.beatport;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;

enum RegexPatterns {
    FEAT(asList(" [fF]eat.? ", " [fF]t.? "), " feat. "), WITH(singletonList(" [wW]ith "), " with "), PRES(singletonList(" [pP]res.? "),
                    " pres. "), AND(asList(" [aA]nd ", " & "), " & "), COMMA(singletonList(", "), ", ");

    RegexPatterns(List<String> patternStrings, String replaceWith) {
        this.replaceWith = replaceWith;
        patternStrings.forEach(r -> patterns.add(Pattern.compile(r)));
    }

    @Getter
    private String replaceWith;
    @Getter
    private List<Pattern> patterns = new ArrayList<>();

    static List<Pattern> getAll() {
        return Arrays.stream(RegexPatterns.values()).flatMap(p -> p.getPatterns().stream()).collect(Collectors.toList());
    }
}
