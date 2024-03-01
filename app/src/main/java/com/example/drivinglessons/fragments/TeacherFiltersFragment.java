package com.example.drivinglessons.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;

public class TeacherFiltersFragment extends Fragment implements Parcelable
{
    private static final String TITLE = "teacher filters", DATA = "data", IS_OWNER = "is owner";

    public static class Data implements Parcelable
    {
        public boolean isManual, isTester;

        public Data(boolean isManual, boolean isTester)
        {
            this.isManual = isManual;
            this.isTester = isTester;
        }

        public Data()
        {
            this(false, false);
        }

        public Data(@NonNull Data data)
        {
            this(data.isManual, data.isTester);
        }

        protected Data(@NonNull Parcel in)
        {
            isManual = in.readByte() == 1;
            isTester = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags)
        {
            dest.writeByte((byte) (isManual ? 1 : 0));
            dest.writeByte((byte) (isTester ? 1 : 0));
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

    private boolean isOwner;
    private Data data;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch manualInput, testerInput;
    private TextView testerText;

    public TeacherFiltersFragment() {}

    @NonNull
    public static TeacherFiltersFragment newInstance(boolean isOwner)
    {
        return newInstance(isOwner, new Data());
    }

    @NonNull
    public static TeacherFiltersFragment newInstance(boolean isOwner, boolean isManual, boolean isTester)
    {
        return newInstance(isOwner, new Data(isManual, isTester));
    }

    @NonNull
    public static TeacherFiltersFragment newInstance(boolean isOwner, Data data)
    {
        TeacherFiltersFragment fragment = new TeacherFiltersFragment();

        Bundle args = new Bundle();
        args.putBoolean(IS_OWNER, isOwner);
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
            isOwner = args.getBoolean(IS_OWNER);
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

        manualInput = view.findViewById(R.id.switchFragmentTeacherFiltersManual);
        testerInput = view.findViewById(R.id.switchFragmentTeacherFiltersTester);
        testerText = view.findViewById(R.id.textViewFragmentTeacherFiltersTester);

        if (isOwner)
        {
            testerInput.setVisibility(View.VISIBLE);
            testerText.setVisibility(View.VISIBLE);
        }

        manualInput.setChecked(data.isManual);
        testerInput.setChecked(data.isTester);

        manualInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isManual = isChecked);
        testerInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isTester = isChecked);
    }

    public Data getData()
    {
        return new Data(data);
    }

    protected TeacherFiltersFragment(@NonNull Parcel in)
    {
        isOwner = in.readByte() == 1;
        data = in.readParcelable(Data.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeByte((byte) (isOwner ? 1 : 0));
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<TeacherFiltersFragment> CREATOR = new Creator<TeacherFiltersFragment>()
    {
        @Override
        public TeacherFiltersFragment createFromParcel(Parcel in)
        {
            return new TeacherFiltersFragment(in);
        }

        @Override
        public TeacherFiltersFragment[] newArray(int size)
        {
            return new TeacherFiltersFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return TITLE;
    }
}
