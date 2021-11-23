package it.ltm.scp.module.android.utils;

import java.util.Comparator;

import it.ltm.scp.module.android.model.devices.printer.gson.InputCustomDataRow;

/**
 * Created by HW64 on 05/05/2017.
 */

public class CustomColumnOrderComparator implements Comparator<InputCustomDataRow> {
    @Override
    public int compare(InputCustomDataRow inputCustomDataRow, InputCustomDataRow t1) {
        return inputCustomDataRow.getIndexColumn() - t1.getIndexColumn();
    }
}
