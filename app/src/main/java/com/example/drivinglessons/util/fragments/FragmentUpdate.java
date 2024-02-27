package com.example.drivinglessons.util.fragments;

import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;

public interface FragmentUpdate
{
    default void update(Student student) {}

    default void update(Teacher teacher) {}
}
