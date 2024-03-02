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

        this.fixDate();

        this.setCancelable(false);
        this.isCanceled = true;
        this.testMode = testMode;
    }

    public LessonDialog(Activity activity, boolean testMode, Date begin, Date end)
    {
        super(activity);
        this.begin = begin;
        this.end = end;

        this.setCancelable(false);
        this.isCanceled = true;
        this.testMode = testMode;
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

        this.add = findViewById(R.id.textViewDialogLessonAdd);
        this.cancel = findViewById(R.id.textViewDialogLessonCancel);

        this.beginTime = findViewById(R.id.textViewDialogLessonBeginTime);
        this.beginDate = findViewById(R.id.textViewDialogLessonBeginDate);
        this.endTime = findViewById(R.id.textViewDialogLessonEndTime);
        this.endDate = findViewById(R.id.textViewDialogLessonEndDate);

        this.updateText();

        this.cancel.setOnClickListener(v -> this.dismiss());
        this.add.setOnClickListener(v ->
        {
            this.isCanceled = false;
            this.dismiss();
        });

        this.beginTime.setOnClickListener(v -> createTimePickerDialog(true));
        this.endTime.setOnClickListener(v -> createTimePickerDialog(false));
        this.beginDate.setOnClickListener(v -> createDatePickerDialog());
        this.endDate.setOnClickListener(v -> createDatePickerDialog());
    }

    private void updateText()
    {
        this.beginTime.setText(Constants.TIME_FORMAT.format(this.begin));
        this.endTime.setText(Constants.TIME_FORMAT.format(this.end));
        this.beginDate.setText(Constants.DATE_FORMAT.format(this.begin));
        this.endDate.setText(Constants.DATE_FORMAT.format(this.end));
    }

    public boolean isCanceled()
    {
        return this.isCanceled;
    }

    public Date getEnd()
    {
        return this.end;
    }

    public Date getBegin()
    {
        return this.begin;
    }

    public double getDuration()
    {
        Duration d = Constants.durationBetween(this.begin, this.end);

        int p_h = (int) d.getSeconds() / (60 * 60), p_m = (int) (d.getSeconds() / 60 - p_h * 60);
        return p_h + p_m / 60.0;
    }

    private void createDatePickerDialog()
    {
        Calendar c = Calendar.getInstance();

        c.setTime(this.begin);

        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) ->
        {
            c.setTime(this.begin);
            c.set(year, month, dayOfMonth);
            this.begin.setTime(c.getTimeInMillis());

            c.setTime(this.end);
            c.set(year, month, dayOfMonth);
            this.end.setTime(c.getTimeInMillis());

            this.updateText();

        }, y, m, d);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void createTimePickerDialog(boolean isBegin)
    {
        Calendar c = Calendar.getInstance();

        Duration d = Constants.durationBetween(this.begin, this.end);

        int p_h = (int) d.getSeconds() / (60 * 60), p_m = (int) (d.getSeconds() / 60 - p_h * 60);

        if (isBegin) c.setTime(this.begin);
        else c.setTime(this.end);

        int h = c.get(Calendar.HOUR_OF_DAY), m = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) ->
        {
            if (isBegin)
            {
                c.setTime(this.begin);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                this.begin.setTime(c.getTimeInMillis());

                c.add(Calendar.HOUR_OF_DAY, p_h);
                c.add(Calendar.MINUTE, p_m);
                this.end.setTime(c.getTimeInMillis());
            }
            else
            {
                c.setTime(this.end);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                this.end.setTime(c.getTimeInMillis());

                if (testMode)
                {
                    c.add(Calendar.HOUR_OF_DAY, -p_h);
                    c.add(Calendar.MINUTE, -p_m);
                    this.begin.setTime(c.getTimeInMillis());
                }
            }

            this.updateText();

        }, h, m, true);

        //timePickerDialog.setMin(MIN_HOUR, -1);
        //if (isBegin) timePickerDialog.setMax(MAX_HOUR - p_h - (p_m == 0 ? 0 : 1), 0);
        //else timePickerDialog.setMax(MAX_HOUR, 0);

        timePickerDialog.show();
    }
}
