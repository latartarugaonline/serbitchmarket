package it.ltm.scp.module.android.ui;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

public class ActionMenuDisabledEditText extends TextInputEditText {
    public ActionMenuDisabledEditText(Context context) {
        super(context);
        setCustomSelectionActionModeCallback();
    }

    public ActionMenuDisabledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomSelectionActionModeCallback();
    }

    public ActionMenuDisabledEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomSelectionActionModeCallback();
    }


    public void setCustomSelectionActionModeCallback() {
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.removeItem(/*android.R.id.shareText*/0x01020035);
                menu.removeItem(/* Translate */0);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

        });
    }
}
