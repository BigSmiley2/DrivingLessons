package com.example.drivinglessons.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.drivinglessons.R;

public class AboutDialog extends Dialog
{
    public AboutDialog(Context context)
    {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstances)
    {
        super.onCreate(savedInstances);
        setContentView(R.layout.dialog_about);
    }
}
