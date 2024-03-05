package com.example.drivinglessons.util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class Constants
{
    public static final String OWNER_EMAIL = "liamizsak@gmail.com";
    public static final double TEST_TIME = 18.66, TEST_COST = 250;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);

    public static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ROOT);

    public static boolean isOwner(String email)
    {
        return OWNER_EMAIL.equals(email);
    }

    public static Period periodBetween(@NonNull Date first, @NonNull Date second)
    {
        LocalDate f = first.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), s = second.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(f, s);
    }

    public static Duration durationBetween(@NonNull Date first, @NonNull Date second)
    {
        return Duration.between(first.toInstant(), second.toInstant());
    }

    @NonNull
    public static String fixName(@NonNull String name)
    {
        if (name.isEmpty()) return "";

        StringBuilder stringBuilder = new StringBuilder(name);

        stringBuilder.setCharAt(0, (char) (stringBuilder.charAt(0) - 'a' + 'A'));

        for (int i = 0; i < stringBuilder.length() - 1; i++)
            if (stringBuilder.charAt(i) == ' ' || stringBuilder.charAt(i) == '-')
                stringBuilder.setCharAt(i + 1, (char) (stringBuilder.charAt(i + 1) - 'a' + 'A'));

        return stringBuilder.toString();
    }

    public static void createAlertDialog(Context context, String message, String positive, String negative, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setPositiveButton(positive, listener);
        builder.setNegativeButton(negative, listener);

        builder.create().show();
    }
}
