package com.example.drivinglessons.util.firebase;

import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.firebase.entities.User;

import java.util.List;

public abstract class FirebaseRunnable
{
    public void run() {}

    public void run(Exception e)
    {
        if (e != null) e.printStackTrace();
    }

    public void run(Void unused) {}

    public void run(User user) {}

    public void run(List<Lesson> lessons) {}

    public void runAll(Exception e, Void unused, User user, List<Lesson> lessons)
    {
        run();
        run(e);
        run(unused);
        run(user);
        run(lessons);
    }

    public void runAll() { runAll(null, null, null, null); }

    public void runAll(Exception e) { runAll(e, null, null, null); }

    public void runAll(Void unused) { runAll(null, unused, null, null); }

    public void runAll(User user) { runAll(null, null, user, null); }

    public void runAll(List<Lesson> lessons) { runAll(null, null, null, lessons); }
}
