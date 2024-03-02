package com.example.drivinglessons.fragments.input;

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

public class InputStudentFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "student", DATA = "data";

    public static class Data implements Parcelable
    {
        public boolean theory;

        public Data(boolean theory)
        {
            this.theory = theory;
        }

        public Data()
        {
            this(false);
        }

        public Data(@NonNull Data data)
        {
            this(data.theory);
        }

        protected Data(@NonNull Parcel in)
        {
            theory = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags)
        {
            dest.writeByte((byte) (theory ? 1 : 0));
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

    public InputStudentFragment() {}

    @NonNull
    public static InputStudentFragment newInstance()
    {
        return newInstance(new Data());
    }
    @NonNull
    public static InputStudentFragment newInstance(boolean theory)
    {
        return newInstance(new Data(theory));
    }

    @NonNull
    public static InputStudentFragment newInstance(Data data)
    {
        InputStudentFragment fragment = new InputStudentFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putParcelable(DATA, data);
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
            data = args.getParcelable(DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_input_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        theoryInput = view.findViewById(R.id.switchFragmentInputStudentTheory);

        theoryInput.setChecked(data.theory);

        theoryInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.theory = isChecked);
    }

    public Data getData()
    {
        return data == null ? null : new Data(data);
    }

    protected InputStudentFragment(@NonNull Parcel in)
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

    public static final Creator<InputStudentFragment> CREATOR = new Creator<InputStudentFragment>()
    {
        @Override
        public InputStudentFragment createFromParcel(Parcel in)
        {
            return new InputStudentFragment(in);
        }

        @Override
        public InputStudentFragment[] newArray(int size)
        {
            return new InputStudentFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
