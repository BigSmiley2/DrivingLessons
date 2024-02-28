package com.example.drivinglessons.dialogs;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.drivinglessons.R;

public class LessonFiltersDialogFragment extends DialogFragment implements Parcelable
{
    private static final String TITLE = "lesson filters", CONFIRMED = "confirmed", PAST = "past", ASSIGNED = "assigned";

    public static class Data implements Parcelable
    {
        public boolean isConfirmed, isPast, isAssigned;

        public Data(boolean isConfirmed, boolean isPast, boolean isAssigned)
        {
            this.isConfirmed = isConfirmed;
            this.isPast = isPast;
            this.isAssigned = isAssigned;
        }

        public Data()
        {
            this(false, false, false);
        }

        public Data(Data data)
        {
            this(data.isConfirmed, data.isPast, data.isAssigned);
        }


        protected Data(Parcel in)
        {
            isConfirmed = in.readByte() == 1;
            isPast = in.readByte() == 1;
            isAssigned = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeByte((byte) (isConfirmed ? 1 : 0));
            dest.writeByte((byte) (isPast ? 1 : 0));
            dest.writeByte((byte) (isAssigned ? 1 : 0));
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

    private Switch confirmInput, pastInput, assignedInput;

    public LessonFiltersDialogFragment() {}

    public static LessonFiltersDialogFragment newInstance()
    {
        return newInstance(false, false, false);
    }

    public static LessonFiltersDialogFragment newInstance(boolean isConfirmed, boolean isPast, boolean isAssigned)
    {
        LessonFiltersDialogFragment fragment = new LessonFiltersDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(CONFIRMED, isConfirmed);
        args.putBoolean(PAST, isPast);
        args.putBoolean(ASSIGNED, isAssigned);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        data = new Data();
        Bundle args = getArguments();
        if (args != null)
        {
            data.isConfirmed = args.getBoolean(CONFIRMED);
            data.isPast = args.getBoolean(PAST);
            data.isAssigned = args.getBoolean(ASSIGNED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_lesson_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        confirmInput = view.findViewById(R.id.switchFragmentLessonFiltersConfirmed);
        pastInput = view.findViewById(R.id.switchFragmentLessonFiltersPast);
        assignedInput = view.findViewById(R.id.switchFragmentLessonFiltersAssigned);

        confirmInput.setChecked(data.isConfirmed);
        pastInput.setChecked(data.isPast);
        assignedInput.setChecked(data.isAssigned);

        confirmInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isConfirmed = isChecked);
        pastInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isPast = isChecked);
        assignedInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isAssigned = isChecked);
    }

    public Data getData()
    {
        return new Data(data);
    }

    protected LessonFiltersDialogFragment(Parcel in)
    {
        data = in.readParcelable(Data.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<LessonFiltersDialogFragment> CREATOR = new Creator<LessonFiltersDialogFragment>()
    {
        @Override
        public LessonFiltersDialogFragment createFromParcel(Parcel in)
        {
            return new LessonFiltersDialogFragment(in);
        }

        @Override
        public LessonFiltersDialogFragment[] newArray(int size)
        {
            return new LessonFiltersDialogFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return TITLE;
    }
}
