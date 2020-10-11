package com.furkanyilmaz.customfonts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.furkanyilmaz.kitapoyla.R;

import java.text.DecimalFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public static int selectYear;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
//        int year = selectYear;
//        int month = selectMonth;
//        int day = selectDay;

//        return new DatePickerDialog(getActivity(), this, year, month, day);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker_dialog, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(year);
        yearPicker.setValue(selectYear);

        builder.setView(dialog).setPositiveButton(Html.fromHtml("<font color='#FF4081'>Seç</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                onDateSet(null, yearPicker.getValue());
            }
        }).setNegativeButton(Html.fromHtml("<font color='#FF4081'>İptal</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatePickerFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    public void onDateSet(DatePicker view, int year) {
        TextView tv = (TextView) getActivity().findViewById(R.id.addbook_text_publishdate);

        changeDate(year);
        tv.setText(String.valueOf(year));
    }

    @Override
    public String toString() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        changeDate(year);
        return String.valueOf(year);
    }

    public static void changeDate(int year) {
        selectYear = year;
    }
}