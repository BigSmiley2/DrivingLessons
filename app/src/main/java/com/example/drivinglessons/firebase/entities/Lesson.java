package com.example.drivinglessons.firebase.entities;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Lesson
{
    private static final String ID = "id", TEACHER_ID = "teacherId", TEACHER_NAME = "teacherName", STUDENT_ID = "studentId", STUDENT_NAME = "studentName", START = "start", END = "end", COST = "cost", IS_CONFIRMED = "isConfirmed", IS_TEST = "isTest";

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

    public Lesson(String studentId, Date start, Date end, Boolean isConfirmed, Boolean isTest)
    {
        this(null, null, null, studentId, null, start, end, null, isConfirmed, isTest);
    }

    public boolean isIntercepting(@NonNull Lesson other)
    {
        return (start.getTime() < other.start.getTime() && end.getTime() > other.start.getTime()) ||
                (other.start.getTime() < start.getTime() && other.end.getTime() > start.getTime());
    }

    public double getDuration()
    {
        Duration d = Duration.between(start.toInstant(), end.toInstant());

        return (double) d.getSeconds() / (60 * 60);
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();

        if (id != null) map.put(ID, id);
        if (teacherId != null) map.put(TEACHER_ID, teacherId);
        if (teacherName != null) map.put(TEACHER_NAME, teacherName);
        if (studentId != null) map.put(STUDENT_ID, studentId);
        if (studentName != null) map.put(STUDENT_NAME, studentName);
        if (start != null) map.put(START, start);
        if (end != null) map.put(END, end);
        if (cost != null) map.put(COST, cost);
        if (isConfirmed != null) map.put(IS_CONFIRMED, isConfirmed);
        if (isTest != null) map.put(IS_TEST, isTest);

        return map;
    }
}
