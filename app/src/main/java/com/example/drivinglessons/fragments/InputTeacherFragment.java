package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;

public class InputTeacherFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "teacher", MANUAL = "manual", TESTER = "tester", COST = "cost";

    private boolean manual, tester;
    private Double cost;

    public InputTeacherFragment() {}

    public static InputTeacherFragment newInstance()
    {
        return newInstance(false, false, null);
    }

    public static InputTeacherFragment newInstance(boolean manual, boolean tester, Double cost)
    {
        InputTeacherFragment fragment = new InputTeacherFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putBoolean(MANUAL, manual);
        args.putBoolean(TESTER, tester);
        args.putSerializable(COST, cost);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            manual = args.getBoolean(MANUAL);
            tester = args.getBoolean(TESTER);
            cost = (Double) args.getSerializable(COST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_input_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    protected InputTeacherFragment(Parcel in)
    {
        manual = in.readByte() == 1;
        tester = in.readByte() == 1;
        cost = in.readByte() == 0 ? null : in.readDouble();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (manual ? 1 : 0));
        dest.writeByte((byte) (tester ? 1 : 0));
        if (cost == null) dest.writeByte((byte) 0);
        else
        {
            dest.writeByte((byte) 1);
            dest.writeDouble(cost);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<InputTeacherFragment> CREATOR = new Creator<InputTeacherFragment>()
    {
        @Override
        public InputTeacherFragment createFromParcel(Parcel in)
        {
            return new InputTeacherFragment(in);
        }

        @Override
        public InputTeacherFragment[] newArray(int size)
        {
            return new InputTeacherFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
