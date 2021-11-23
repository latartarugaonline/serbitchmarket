package it.ltm.scp.module.android.model.devices.printer.gson;

import java.util.List;

/**
 * Created by HW64 on 02/11/2016.
 */
public class InputDataRow {

    private Integer columnGroup;
    private List<InputDataRowItem> columns;

    public Integer getColumnGroup() {
        return columnGroup;
    }

    public void setColumnGroup(Integer columnGroup) {
        this.columnGroup = columnGroup;
    }

    public List<InputDataRowItem> getColumns() {
        return columns;
    }

    public void setColumns(List<InputDataRowItem> columns) {
        this.columns = columns;
    }


}
