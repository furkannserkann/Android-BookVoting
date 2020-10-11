package com.furkanyilmaz.customfonts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.user.ActivityUser;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerPubDate extends DialogFragment {

    public static int selectYear=0;

    public static TextView textView;
    public static Button button;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
//        int year = selectYear;
//        int month = selectMonth;
//        int day = selectDay;
//
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
                DatePickerPubDate.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    public void onDateSet(DatePicker view, int year) {
//        TextView tv = (TextView) dialog.findViewById(R.id.bookfiltre_text_date1);
        boolean donus = true;

        if (textView.getId() == R.id.bookfiltre_text_date1) {
            if (ActivityUser.pubDate2 != null) {
                if (year > ActivityUser.pubDate2) {
                    Toast.makeText(getActivity(), "Başlangıç Tarihi Büyük Olamaz Bitiş Tarihi", Toast.LENGTH_SHORT).show();
                    donus = false;
                }
            }
        } else if (textView.getId() == R.id.bookfiltre_text_date2) {
            if (ActivityUser.pubDate1 != null) {
                if (year < ActivityUser.pubDate1) {
                    Toast.makeText(getActivity(), "Başlangıç Tarihi Büyük Olamaz Bitiş Tarihi", Toast.LENGTH_SHORT).show();
                    donus = false;
                }
            }
        }

        if (donus) {
            changeDate(year);
            textView.setText(String.valueOf(year));

            if (textView.getId() == R.id.bookfiltre_text_date1) {
                ActivityUser.pubDate1 = year;
            } else if (textView.getId() == R.id.bookfiltre_text_date2) {
                ActivityUser.pubDate2 = year;
            }

            if (button != null) {
                button.setBackgroundResource(R.drawable.blue_border_rounded_red);
                button.setText("Temizle");
            }
        }
    }

    public static void changeDate(int year) {
        selectYear = year;
    }
}