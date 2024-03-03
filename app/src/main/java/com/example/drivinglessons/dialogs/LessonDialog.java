package com.example.drivinglessons.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drivinglessons.R;
import com.example.drivinglessons.util.Constants;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LessonDialog extends Dialog
{
    private static final int MIN_HOUR = 8, MAX_HOUR = 18;

    private final Date start, end;
    private TextView startTime, startDate, endTime, endDate, cancel, add;

    private boolean isCanceled;
    private final boolean testMode;
    public LessonDialog(Context context, boolean testMode)
    {
        super(context);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MINUTE, 30);
        this.start = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        this.end = calendar.getTime();
        this.testMode = testMode;

        fixDate(start, end);

        setCancelable(false);
        isCanceled = true;
    }

    public LessonDialog(Activity activity, boolean testMode, Date start, Date end)
    {
        super(activity);
        this.start = start;
        this.end = end;
        this.testMode = testMode;

        setCancelable(false);
        isCanceled = true;

    }

    private boolean fixDate(Date start, Date end)
    {
        boolean isFixed;
        Calendar calendar = Calendar.getInstance();
        isFixed = fixDate(start);
        isFixed |= fixDate(end);
        if (start.getTime() == end.getTime())
        {
            calendar.setTime(start);
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            start.setTime(calendar.getTimeInMillis());
            calendar.setTime(end);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            end.setTime(calendar.getTimeInMillis());
            isFixed |= fixDate(start);
            isFixed |= fixDate(end);
        }
        return isFixed;
    }

    private boolean fixDate(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < MIN_HOUR) {
            calendar.set(Calendar.HOUR_OF_DAY, MIN_HOUR);
            calendar.set(Calendar.MINUTE, 0);
            date.setTime(calendar.getTimeInMillis());
            return true;
        } else if (hour > MAX_HOUR || hour == MAX_HOUR && calendar.get(Calendar.MINUTE) > 0) {
            calendar.set(Calendar.HOUR_OF_DAY, MAX_HOUR);
            calendar.set(Calendar.MINUTE, 0);
            date.setTime(calendar.getTimeInMillis());
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstances)
    {
        super.onCreate(savedInstances);
        setContentView(R.layout.dialog_lesson);

        //this.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        add = findViewById(R.id.textViewDialogLessonAdd);
        cancel = findViewById(R.id.textViewDialogLessonCancel);
        startTime = findViewById(R.id.textViewDialogLessonBeginTime);
        startDate = findViewById(R.id.textViewDialogLessonBeginDate);
        endTime = findViewById(R.id.textViewDialogLessonEndTime);
        endDate = findViewById(R.id.textViewDialogLessonEndDate);

        updateText();

        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v ->
        {
            isCanceled = false;
            dismiss();
        });

        startTime.setOnClickListener(v -> createTimePickerDialog(true));
        endTime.setOnClickListener(v -> createTimePickerDialog(false));
        startDate.setOnClickListener(v -> createDatePickerDialog());
        endDate.setOnClickListener(v -> createDatePickerDialog());
    }

    private void updateText()
    {
        startTime.setText(Constants.TIME_FORMAT.format(start));
        endTime.setText(Constants.TIME_FORMAT.format(end));
        startDate.setText(Constants.DATE_FORMAT.format(start));
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

    public Date getStart()
    {
        return start;
    }

    public double getDuration()
    {
        Duration d = Constants.durationBetween(start, end);

        int p_h = (int) d.getSeconds() / (60 * 60), p_m = (int) (d.getSeconds() / 60 - p_h * 60);
        return p_h + p_m / 60.0;
    }

    private void createDatePickerDialog()
    {
        Calendar c = Calendar.getInstance();

        c.setTime(start);

        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) ->
        {
            c.setTime(start);
            c.set(year, month, dayOfMonth);
            start.setTime(c.getTimeInMillis());

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

    private void createTimePickerDialog(boolean isStart)
    {
        Calendar c = Calendar.getInstance();

        Duration d = Constants.durationBetween(start, end);

        int p_h = (int) d.getSeconds() / (60 * 60), p_m = (int) (d.getSeconds() / 60 - p_h * 60);

        if (isStart) c.setTime(start);
        else c.setTime(end);

        int h = c.get(Calendar.HOUR_OF_DAY), m = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) ->
        {
            Date start = new Date(), end = new Date();
            if (isStart)
            {
                c.setTime(this.start);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                start.setTime(c.getTimeInMillis());

                c.add(Calendar.HOUR_OF_DAY, p_h);
                c.add(Calendar.MINUTE, p_m);
                end.setTime(c.getTimeInMillis());
            }
            else
            {
                c.setTime(this.end);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                end.setTime(c.getTimeInMillis());

                if (testMode)
                {
                    c.add(Calendar.HOUR_OF_DAY, - p_h);
                    c.add(Calendar.MINUTE, - p_m);
                    start.setTime(c.getTimeInMillis());
                }
            }
            if (fixDate(start, end)) Toast.makeText(getContext(), String.format(Locale.ROOT, "Lesson must be between\n0%d:00 - %d:00", MIN_HOUR, MAX_HOUR), Toast.LENGTH_SHORT).show();
            else
            {
                this.start.setTime(start.getTime());
                this.end.setTime(end.getTime());
            }
            updateText();

        }, h, m, true);

        timePickerDialog.show();
    }
}
