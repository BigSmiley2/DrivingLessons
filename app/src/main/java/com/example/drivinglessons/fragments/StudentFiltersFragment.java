package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;

public class StudentFiltersFragment extends Fragment implements Parcelable
{
    private static final String TITLE = "student filters", DATA = "data";

    public static class Data implements Parcelable
    {
        public boolean isTheory;

        public Data(boolean isTheory)
        {
            this.isTheory = isTheory;
        }

        public Data()
        {
            this(false);
        }

        public Data(@NonNull Data data)
        {
            this(data.isTheory);
        }

        protected Data(@NonNull Parcel in)
        {
            isTheory = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags)
        {
            dest.writeByte((byte) (isTheory ? 1 : 0));
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<Data> CREATOR = new Creator<Data>()
        {
            @Override
            public Data createFromParcel(Parcel in)
            {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size)
            {
                return new Data[size];
            }
        };
    }

    private Data data;

    private Switch theoryInput;

    public StudentFiltersFragment() {}

    @NonNull
    public static StudentFiltersFragment newInstance()
    {
        return newInstance(new Data());
    }

    @NonNull
    public static StudentFiltersFragment newInstance(boolean isTheory)
    {
        return newInstance(new Data(isTheory));
    }

    @NonNull
    public static StudentFiltersFragment newInstance(Data data)
    {
        StudentFiltersFragment fragment = new StudentFiltersFragment();

        Bundle args = new Bundle();
        args.putParcelable(DATA, data);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            data = args.getParcelable(DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_teacher_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        theoryInput = view.findViewById(R.id.switchFragmentStudentFiltersTheory);

        theoryInput.setChecked(data.isTheory);

        theoryInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isTheory = isChecked);
    }

    public Data getData()
    {
        return new Data(data);
    }

    protected StudentFiltersFragment(@NonNull Parcel in)
    {
        data = in.readParcelable(Data.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<StudentFiltersFragment> CREATOR = new Creator<StudentFiltersFragment>()
    {
        @Override
        public StudentFiltersFragment createFromParcel(Parcel in)
        {
            return new StudentFiltersFragment(in);
        }

        @Override
        public StudentFiltersFragment[] newArray(int size)
        {
            return new StudentFiltersFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return TITLE;
    }
}
