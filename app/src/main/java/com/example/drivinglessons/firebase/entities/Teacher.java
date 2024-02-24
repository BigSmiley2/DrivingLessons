package com.example.drivinglessons.firebase.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Map;

public class Teacher extends User implements Parcelable
{
    private static final String HAS_MANUAL = "hasManual", IS_TESTER = "isTester", COST_PER_HOUR = "costPerHour", SENIORITY = "seniority";
    public Boolean hasManual, isTester;
    public Double costPerHour;
    public Date seniority;

    public Teacher() {}

    public Teacher(String id, String name, String email, String password, String imagePath, Date birthdate, Boolean hasManual, Boolean isTester, Double costPerHour, Date seniority)
    {
        super(id, name, email, password, imagePath, birthdate);
        this.hasManual = hasManual;
        this.isTester = isTester;
        this.costPerHour = costPerHour;
        this.seniority = seniority;
    }

    protected Teacher(Parcel in)
    {
        super(in);
        hasManual = in.readByte() == 1;
        isTester = in.readByte() == 1;
        costPerHour = in.readDouble();
        seniority = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (hasManual ? 1 : 0));
        dest.writeByte((byte) (isTester ? 1 : 0));
        dest.writeDouble(costPerHour);
        dest.writeSerializable(seniority);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }


    public static final Creator<Teacher> CREATOR = new Creator<Teacher>()
    {
        @Override
        public Teacher createFromParcel(Parcel in)
        {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size)
        {
            return new Teacher[size];
        }
    };

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> map = super.toMap();
        if (hasManual != null) map.put(HAS_MANUAL, hasManual);
        if (isTester != null) map.put(IS_TESTER, isTester);
        if (costPerHour != null) map.put(COST_PER_HOUR, costPerHour);
        if (seniority != null) map.put(SENIORITY, seniority);
        return map;
    }
}
