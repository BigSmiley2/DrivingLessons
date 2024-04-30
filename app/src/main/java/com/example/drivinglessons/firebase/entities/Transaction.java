package com.example.drivinglessons.firebase.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction implements Parcelable
{
    private static final String ID = "id", FROM_ID = "fromId", FROM_NAME = "fromName", TO_ID = "toId", TO_NAME = "toName", DATE = "date", AMOUNT = "amount", IS_CONFIRMED = "isConfirmed";

    public String id, fromId, toId, fromName, toName;
    public Date date;
    public Double amount;
    public Boolean isConfirmed;

    public Transaction() {}

    public Transaction(String id, String fromId, String toId, String fromName, String toName, Date date, Double amount, Boolean isConfirmed)
    {
        this.id = id;
        this.fromId = fromId;
        this.fromName = fromName;
        this.toId = toId;
        this.toName = toName;
        this.date = date;
        this.amount = amount;
        this.isConfirmed = isConfirmed;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();

        if (id != null) map.put(ID, id);
        if (fromId != null) map.put(FROM_ID, fromId);
        if (fromName != null) map.put(FROM_NAME, fromName);
        if (toId != null) map.put(TO_ID, toId);
        if (toName != null) map.put(TO_NAME, toName);
        if (date != null) map.put(DATE, date);
        if (amount != null) map.put(AMOUNT, amount);
        if (isConfirmed != null) map.put(IS_CONFIRMED, isConfirmed);

        return map;
    }

    protected Transaction(@NonNull Parcel in)
    {
        id = in.readString();
        fromId = in.readString();
        toId = in.readString();
        fromName = in.readString();
        toName = in.readString();
        if (in.readByte() == 0)
            amount = null;
        else amount = in.readDouble();
        byte tmpIsConfirmed = in.readByte();
        isConfirmed = tmpIsConfirmed == 0 ? null : tmpIsConfirmed == 1;
        date = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(fromId);
        dest.writeString(toId);
        dest.writeString(fromName);
        dest.writeString(toName);
        if (amount == null)
            dest.writeByte((byte) 0);
        else
        {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        dest.writeByte((byte) (isConfirmed == null ? 0 : isConfirmed ? 1 : 2));
        dest.writeSerializable(date);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>()
    {
        @Override
        public Transaction createFromParcel(Parcel in)
        {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size)
        {
            return new Transaction[size];
        }
    };
}
