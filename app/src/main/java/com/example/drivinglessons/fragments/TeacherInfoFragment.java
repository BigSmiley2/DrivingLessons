package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.util.fragments.FragmentUpdate;

import java.util.Locale;

public class TeacherInfoFragment extends Fragment implements FragmentUpdate, Parcelable
{
    private static final String FRAGMENT_TITLE = "teacher info";

    private TextView manual, tester, cost, seniority;

    public TeacherInfoFragment() {}

    @NonNull
    public static TeacherInfoFragment newInstance()
    {
        return new TeacherInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_teacher_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        manual = view.findViewById(R.id.textViewFragmentTeacherInfoManual);
        tester = view.findViewById(R.id.textViewFragmentTeacherInfoTester);
        cost = view.findViewById(R.id.editTextFragmentInputTeacherCost);
        seniority = view.findViewById(R.id.textViewFragmentTeacherInfoSeniority);
    }

    @Override
    public void update(@NonNull Teacher teacher)
    {
        manual.setText(String.format(Locale.ROOT, "%s has %s car", teacher.name, teacher.hasManual ? "a manual" : "an automatic"));
        tester.setText(String.format(Locale.ROOT, "%s is a %s", teacher.name, teacher.isTester ? "tester" : "teacher"));
        cost.setText(String.format(Locale.ROOT, "%s takes %.2f₪ per hour", teacher.name, teacher.costPerHour));
    }

    protected TeacherInfoFragment(Parcel in) {}

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {}

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<TeacherInfoFragment> CREATOR = new Creator<TeacherInfoFragment>()
    {
        @Override
        public TeacherInfoFragment createFromParcel(Parcel in)
        {
            return new TeacherInfoFragment(in);
        }

        @Override
        public TeacherInfoFragment[] newArray(int size)
        {
            return new TeacherInfoFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
