package com.example.drivinglessons.util.validation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class Permission
{
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static boolean check(Context context)
    {
        for (String permission : PERMISSIONS)
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) return false;
        return true;
    }

    public static boolean request(Activity activity)
    {
        ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
        return false;
    }

    /** @noinspection BooleanMethodIsAlwaysInverted*/
    public static boolean isAllowed(Activity activity)
    {
        return check(activity) || request(activity);
    }

    public static boolean checkNotify(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_DENIED;
        else return true;
    }

    public static boolean requestNotify(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        return false;
    }

    /** @noinspection BooleanMethodIsAlwaysInverted*/
    public static boolean isAllowedNotify(Activity activity)
    {
        return checkNotify(activity) || requestNotify(activity);
    }
}
