package com.example.drivinglessons.fragments.info;

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
import com.example.drivinglessons.firebase.entities.Student;

import java.util.Locale;

public class StudentInfoFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "student info";

    private TextView theory;

    public StudentInfoFragment() {}

    @NonNull
    public static StudentInfoFragment newInstance()
    {
        return new StudentInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_student_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        theory = view.findViewById(R.id.textViewFragmentStudentInfoTheory);
    }

    public void update(@NonNull Student student)
    {
        theory.setText(String.format(Locale.ROOT, "%s %s a driving theory test", student.name, student.hasTheoryTest ? "passed" : "didn't pass"));
    }

    protected StudentInfoFragment(Parcel in) {}

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {}

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<StudentInfoFragment> CREATOR = new Creator<StudentInfoFragment>()
    {
        @Override
        public StudentInfoFragment createFromParcel(Parcel in)
        {
            return new StudentInfoFragment(in);
        }

        @Override
        public StudentInfoFragment[] newArray(int size)
        {
            return new StudentInfoFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
