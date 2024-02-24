package com.example.drivinglessons.firebase.entities;

import java.time.Duration;
import java.util.Date;

public class Lesson
{
     public String id, teacherId, studentId, teacherName, studentName;
     public Date start, end;
     public Double cost;
     public Boolean isConfirmed, isTest;

     public Lesson() {}

    public Lesson(String id, String teacherId, String teacherName, String studentId, String studentName, Date start, Date end, Double cost, Boolean isConfirmed, Boolean isTest)
    {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.start = start;
        this.end = end;
        this.cost = cost;
        this.isConfirmed = isConfirmed;
        this.isTest = isTest;
    }

    public boolean isIntercepting(Lesson other)
    {
        return (start.getTime() < other.start.getTime() && end.getTime() > other.start.getTime()) ||
                (other.start.getTime() < start.getTime() && other.end.getTime() > start.getTime());
    }

    public double getDuration()
    {
        Duration d = Duration.between(start.toInstant(), end.toInstant());

        return (double) d.getSeconds() / (60 * 60);
    }
}
