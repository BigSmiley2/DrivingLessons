package com.example.drivinglessons.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SharedPreferencesManager
{
    private static final String SHARED_PREFERENCES_TITLE = "user", IS_STUDENT = "isStudent", HAS_TEACHER = "hasTeacher", CAN_TEST = "canTest", IS_OWNER = "isOwner";
    private static SharedPreferencesManager spm;
    private final SharedPreferences sp;
    private final SharedPreferences.Editor edit;
    private SharedPreferencesManager(@NonNull Context context)
    {
        sp = context.getSharedPreferences(SHARED_PREFERENCES_TITLE, Context.MODE_PRIVATE);
        edit = sp.edit();
    }

    public static SharedPreferencesManager getInstance(Context context)
    {
        return spm == null ? spm = new SharedPreferencesManager(context) : spm;
    }

    public void put(Boolean isStudent, Boolean hasTeacher, Boolean canTest, Boolean isOwner)
    {
        if (isStudent != null) putIsStudent(isStudent);
        if (hasTeacher != null) putHasTeacher(hasTeacher);
        if (canTest != null) putCanTest(canTest);
        if (isOwner != null) putIsOwner(isOwner);
    }

    public void putIsStudent(Boolean isStudent)
    {
        edit.putBoolean(IS_STUDENT, isStudent).apply();
    }

    public boolean getIsStudent()
    {
        return sp.getBoolean(IS_STUDENT, false);
    }

    public void putHasTeacher(Boolean hasTeacher)
    {
        edit.putBoolean(HAS_TEACHER, hasTeacher).apply();
    }

    public boolean getHasTeacher()
    {
        return sp.getBoolean(HAS_TEACHER, false);
    }

    public void putCanTest(Boolean canTest)
    {
        edit.putBoolean(CAN_TEST, canTest).apply();
    }

    public boolean getCanTest()
    {
        return sp.getBoolean(CAN_TEST, false);
    }

    public void putIsOwner(Boolean isOwner)
    {
        edit.putBoolean(IS_OWNER, isOwner).apply();
    }

    public boolean getIsOwner()
    {
        return sp.getBoolean(IS_OWNER, false);
    }

    public void clear()
    {
        edit.clear().apply();
    }
}
