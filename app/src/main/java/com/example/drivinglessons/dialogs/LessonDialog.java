package com.example.drivinglessons.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.drivinglessons.R;
import com.example.drivinglessons.util.Constants;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class LessonDialog extends Dialog
{
    private static final int MIN_HOUR = 8, MAX_HOUR = 18;

    private final Date begin, end;
    private TextView beginTime, beginDate, endTime, endDate, cancel, add;

    private boolean isCanceled, testMode;
    public LessonDialog(Context context, boolean testMode)
    {
        super(context);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MINUTE, 30);
        this.begin = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        this.end = calendar.getTime();
        this.testMode = testMode;

        fixDate();

        setCancelable(false);
        isCanceled = true;
    }

    public LessonDialog(Activity activity, boolean testMode, Date begin, Date end)
    {
        super(activity);
        this.begin = begin;
        this.end = end;
        this.testMode = testMode;

        setCancelable(false);
        isCanceled = true;

    }

    private void fixDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);
        if (calendar.get(Calendar.HOUR_OF_DAY) < MIN_HOUR)
            fixDate(calendar);
        calendar.setTime(end);
        if (MAX_HOUR < calendar.get(Calendar.HOUR_OF_DAY))
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            fixDate(calendar);
        }
    }

    private void fixDate(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY, MIN_HOUR);
        begin.setTime(calendar.getTimeInMillis());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        end.setTime(calendar.getTimeInMillis());
    }

    @Override
    protected void onCreate(Bundle savedInstances)
    {
        super.onCreate(savedInstances);
        setContentView(R.layout.dialog_lesson);

        //this.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        add = findViewById(R.id.textViewDialogLessonAdd);
        cancel = findViewById(R.id.textViewDialogLessonCancel);
        beginTime = findViewById(R.id.textViewDialogLessonBeginTime);
        beginDate = findViewById(R.id.textViewDialogLessonBeginDate);
        endTime = findViewById(R.id.textViewDialogLessonEndTime);
        endDate = findViewById(R.id.textViewDialogLessonEndDate);

        updateText();

        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v ->
        {
            isCanceled = false;
            dismiss();
        });

        beginTime.setOnClickListener(v -> createTimePickerDialog(true));
        endTime.setOnClickListener(v -> createTimePickerDialog(false));
        beginDate.setOnClickListener(v -> createDatePickerDialog());
        endDate.setOnClickListener(v -> createDatePickerDialog());
    }

    private void updateText()
    {
        beginTime.setText(Constants.TIME_FORMAT.format(begin));
        endTime.setText(Constants.TIME_FORMAT.format(end));
        beginDate.setText(Constants.DATE_FORMAT.format(begin));
        endDate.setText(Constants.DATE_FORMAT.format(end));
    }

    public boolean isCanceled()
    {
        return isCanceled;
    }

    public Date getEnd()
    {
        return end;
    }

    public Date getBegin()
    {
        return begin;
    }

    public double getDuration()
    {
        Duration d = Constants.durationBetween(begin, end);

        int p_h = (int) d.getSeconds() / (60 * 60), p_m = (int) (d.getSeconds() / 60 - p_h * 60);
        return p_h + p_m / 60.0;
    }

    private void createDatePickerDialog()
    {
        Calendar c = Calendar.getInstance();

        c.setTime(begin);

        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) ->
        {
            c.setTime(begin);
            c.set(year, month, dayOfMonth);
            begin.setTime(c.getTimeInMillis());

            c.setTime(end);
            c.set(year, month, dayOfMonth);
            end.setTime(c.getTimeInMillis());

            updateText();

        }, y, m, d);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void createTimePickerDialog(boolean isBegin)
    {
        Calendar c = Calendar.getInstance();

        Duration d = Constants.durationBetween(begin, end);

        int p_h = (int) d.getSeconds() / (60 * 60), p_m = (int) (d.getSeconds() / 60 - p_h * 60);

        if (isBegin) c.setTime(begin);
        else c.setTime(end);

        int h = c.get(Calendar.HOUR_OF_DAY), m = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) ->
        {
            if (isBegin)
            {
                c.setTime(begin);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                begin.setTime(c.getTimeInMillis());

                c.add(Calendar.HOUR_OF_DAY, p_h);
                c.add(Calendar.MINUTE, p_m);
                end.setTime(c.getTimeInMillis());
            }
            else
            {
                c.setTime(end);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                end.setTime(c.getTimeInMillis());

                if (testMode)
                {
                    c.add(Calendar.HOUR_OF_DAY, - p_h);
                    c.add(Calendar.MINUTE, - p_m);
                    begin.setTime(c.getTimeInMillis());
                }
            }
            fixDate();
            updateText();

        }, h, m, true);

        timePickerDialog.show();
    }
}
