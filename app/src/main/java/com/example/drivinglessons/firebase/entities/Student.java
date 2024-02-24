package com.example.drivinglessons.firebase.entities;

import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Map;

public class Student extends User
{
    private static final String HAS_THEORY_TEST = "hasTheoryTest", TEACHER_ID = "teacherId";
    public Boolean hasTheoryTest;
    public String teacherId;

    public Student() {}

    public Student(String id, String name, String email, String password, String imagePath, Date birthdate, Boolean hasTheoryTest)
    {
        super(id, name, email, password, imagePath, birthdate);
        this.hasTheoryTest = hasTheoryTest;
    }

    public Student(String id, String name, String email, String password, String imagePath, Date birthdate, Boolean hasTheoryTest, String teacherId)
    {
        this(id, name, email, password, imagePath, birthdate, hasTheoryTest);
        this.teacherId = teacherId;
    }

    protected Student(Parcel in)
    {
        super(in);
        hasTheoryTest = in.readByte() == 1;
        teacherId = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (hasTheoryTest ? 1 : 0));
        dest.writeString(teacherId);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }


    public static final Creator<Student> CREATOR = new Creator<Student>()
    {
        @Override
        public Student createFromParcel(Parcel in)
        {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size)
        {
            return new Student[size];
        }
    };


    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> map = super.toMap();
        if (hasTheoryTest != null) map.put(HAS_THEORY_TEST, hasTheoryTest);
        if (teacherId != null) map.put(TEACHER_ID, teacherId);
        return map;
    }
}
