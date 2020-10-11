package com.furkanyilmaz.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RadioButton;

public class RadioButton_Poppins_Regular extends RadioButton {

    public RadioButton_Poppins_Regular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RadioButton_Poppins_Regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadioButton_Poppins_Regular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
            setTypeface(tf);
        }
    }

}