package com.example.drivinglessons.util;

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
    public static final double TEST_TIME = 18.66;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);

    public static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ROOT);

    public static boolean isOwner(String email)
    {
        return OWNER_EMAIL.equals(email);
    }

    public static Period periodBetween(Date first, Date second)
    {
        LocalDate f = first.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), s = second.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(f, s);
    }

    public static Duration durationBetween(Date first, Date second)
    {
        return Duration.between(first.toInstant(), second.toInstant());
    }
}
