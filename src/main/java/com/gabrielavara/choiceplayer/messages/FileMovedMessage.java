package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.util.Opinion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileMovedMessage {
    private final String title;
    private final Opinion opinion;
}
