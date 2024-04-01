package com.example.drivinglessons.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.drivinglessons.MainActivity;
import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Rating;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.validation.Permission;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NotificationService extends Service
{
    private static final String CHANNEL_ID_RATINGS = "channel id notification ratings", CHANNEL_ID_LESSONS = "channel id notification lessons";
    public static final String ID = "id", IS_STUDENT = "is student";
    private static final int idRatings = 0, idLessons = 1;

    private FirebaseManager fm;
    private ValueEventListener listenerRatings, listenerLessons;
    private String id;
    private boolean isStudent;
    private boolean lessonState, ratingState;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        fm = FirebaseManager.getInstance(getBaseContext());
        lessonState = false;
        ratingState = false;

        listenerRatings = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (ratingState) makeNotifications("You have new ratings", idRatings);

                ratingState = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        listenerLessons = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (lessonState) makeNotifications("Your lessons state has changed", idLessons);

                lessonState = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }

    public void makeNotifications(String message, int id)
    {
        if (!Permission.checkNotify(getApplicationContext())) return;

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_RATINGS)
                .setSmallIcon(R.drawable.car_icon)
                .setContentTitle("Driving Lessons")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        createNotificationChannel(builder, id);

    }

    private void createNotificationChannel(@NonNull NotificationCompat.Builder builder, int id)
    {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_RATINGS, "ratings notification", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(id);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(id, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        id = intent.getStringExtra(ID);
        isStudent = intent.getBooleanExtra(IS_STUDENT, false);

        fm.getRatingQuery(id).addValueEventListener(listenerRatings);
        fm.getUserLessonsQuery(isStudent, id).addValueEventListener(listenerLessons);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        fm.getRatingQuery(id).removeEventListener(listenerRatings);
        fm.getUserLessonsQuery(isStudent, id).removeEventListener(listenerLessons);
    }
}