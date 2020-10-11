package com.furkanyilmaz.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Spinner;

public class Spinner_Poppins_Regular extends androidx.appcompat.widget.AppCompatSpinner {

    public Spinner_Poppins_Regular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Spinner_Poppins_Regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Spinner_Poppins_Regular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
//            setTypeface(tf);
        }
    }

}