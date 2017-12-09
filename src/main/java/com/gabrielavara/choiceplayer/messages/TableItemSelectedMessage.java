package com.gabrielavara.choiceplayer.messages;

import com.gabrielavara.choiceplayer.views.TableItem;
import lombok.Getter;

public class TableItemSelectedMessage {
    @Getter
    private TableItem tableItem;

    public TableItemSelectedMessage(TableItem tableItem) {
        this.tableItem = tableItem;
    }
}
