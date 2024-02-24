package com.example.drivinglessons.util.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.firebase.entities.User;

import java.util.List;

public interface FirebaseRunnable extends Parcelable
{
    default void run() {}

    default void run(Exception e)
    {
        if (e != null) e.printStackTrace();
    }

    default void run(Void unused) {}

    default void run(User user) {}

    default void run(List<Lesson> lessons) {}

    default void runAll(Exception e, Void unused, User user, List<Lesson> lessons)
    {
        run();
        run(e);
        run(unused);
        run(user);
        run(lessons);
    }

    default void runAll() { runAll(null, null, null, null); }

    default void runAll(Exception e) { runAll(e, null, null, null); }

    default void runAll(Void unused) { runAll(null, unused, null, null); }

    default void runAll(User user) { runAll(null, null, user, null); }

    default void runAll(List<Lesson> lessons) { runAll(null, null, null, lessons); }

    @Override
    default int describeContents()
    {
        return 0;
    }

    @Override
    default void writeToParcel(@NonNull Parcel parcel, int i) {}
}
