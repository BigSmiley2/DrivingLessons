package com.example.drivinglessons.util.validation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

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

    public static boolean isAllowed(Activity activity)
    {
        return check(activity) || request(activity);
    }
}
