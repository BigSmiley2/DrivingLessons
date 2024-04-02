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
import com.example.drivinglessons.firebase.entities.Rating;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;

import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TeacherInfoFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "teacher info";

    private FirebaseManager fm;

    private TextView manual, tester, cost, seniority, rating;

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

        fm = FirebaseManager.getInstance(requireContext());

        manual = view.findViewById(R.id.textViewFragmentTeacherInfoManual);
        tester = view.findViewById(R.id.textViewFragmentTeacherInfoTester);
        cost = view.findViewById(R.id.textViewFragmentTeacherInfoCost);
        seniority = view.findViewById(R.id.textViewFragmentTeacherInfoSeniorityData);
        rating = view.findViewById(R.id.textViewFragmentTeacherInfoRatingData);
    }

    public void update(@NonNull Teacher teacher)
    {
        Period period = Constants.periodBetween(teacher.seniority, Calendar.getInstance().getTime());

        manual.setText(String.format(Locale.ROOT, "%s has %s car", teacher.name, teacher.hasManual ? "a manual" : "an automatic"));
        tester.setText(String.format(Locale.ROOT, "%s is a %s", teacher.name, teacher.isTester ? "tester" : "teacher"));
        cost.setText(String.format(Locale.ROOT, "%s takes %.2f₪ per hour", teacher.name, teacher.costPerHour));
        seniority.setText(String.format(Locale.ROOT, "%d.%d.%d", period.getYears(), period.getMonths(), period.getDays()));

        fm.getUserRatings(teacher.id, new FirebaseRunnable()
        {
            @Override
            public void run(List<?> list)
            {
                List<Rating> ratings = new ArrayList<>();
                for (Object obj : list)
                    ratings.add((Rating) obj);

                int count = ratings.size();

                if (count == 0) rating.setText("?");
                else
                {
                    double sum = 0;

                    for (Rating rating : ratings)
                        sum += rating.rate;

                    rating.setText(String.format(Locale.ROOT, "%.2f", sum / count));
                }
            }
        }, new FirebaseRunnable() {});
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
