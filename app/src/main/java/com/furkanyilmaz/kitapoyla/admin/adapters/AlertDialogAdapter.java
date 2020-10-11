package com.furkanyilmaz.kitapoyla.admin.adapters;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogAdapter extends AlertDialog.Builder {
    public AlertDialogAdapter(Context context) {
        super(context);
    }

    public AlertDialogAdapter(Context context, int themeResId) {
        super(context, themeResId);
    }
}
