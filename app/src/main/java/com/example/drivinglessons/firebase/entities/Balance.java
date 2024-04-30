package com.example.drivinglessons.firebase.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Balance implements Parcelable
{
    private static final String DATE = "date", AMOUNT = "amount";

    public Date date;
    public Double amount;

    public Balance() {}

    public Balance(Double amount, Date date)
    {
        this.amount = amount;
        this.date = date;
    }

    protected Balance(Parcel in)
    {
        if (in.readByte() == 0)
            amount = null;
        else
        {
            amount = in.readDouble();
        }
        date = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        if (amount == null)
            dest.writeByte((byte) 0);
        else
        {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        dest.writeSerializable(date);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Balance> CREATOR = new Creator<Balance>()
    {
        @Override
        public Balance createFromParcel(Parcel in)
        {
            return new Balance(in);
        }

        @Override
        public Balance[] newArray(int size)
        {
            return new Balance[size];
        }
    };

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();

        if (date != null) map.put(DATE, date);
        if (amount != null) map.put(AMOUNT, amount);

        return map;
    }
}
