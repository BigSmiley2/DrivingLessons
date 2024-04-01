package com.example.drivinglessons.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.example.drivinglessons.R;

public class NetworkChangedReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
        {
            boolean isNetworkAvailable = Constants.isNetworkAvailable(context);

            Toast.makeText(context, isNetworkAvailable ? R.string.online : R.string.offline, Toast.LENGTH_SHORT).show();
        }
    }
}
