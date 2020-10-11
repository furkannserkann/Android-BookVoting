package com.furkanyilmaz.kitapoyla.custom;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.furkanyilmaz.items.User;
import com.furkanyilmaz.kitapoyla.R;

public class myStaticObj {

    public static User loginUser;

    public static void setValidColor(Context context, RelativeLayout rl, boolean isBlue) {
        if (isBlue) {
            rl.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
        } else {
            rl.setBackgroundResource(R.drawable.red_border_rounded_cornwe);
            rl.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake));
        }
    }

    public static void setValidColor(Context context, LinearLayout rl, boolean isBlue) {
        if (isBlue) {
            rl.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
        } else {
            rl.setBackgroundResource(R.drawable.red_border_rounded_cornwe);
            rl.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake));
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
