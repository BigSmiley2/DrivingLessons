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

public class InputStudentFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "student", THEORY = "theory";

    private boolean theory;

    private Switch theoryInput;

    public InputStudentFragment() {}

    public static InputStudentFragment newInstance()
    {
        return newInstance(true);
    }
    public static InputStudentFragment newInstance(boolean theory)
    {
        InputStudentFragment fragment = new InputStudentFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putBoolean(THEORY, theory);
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
            theory = args.getBoolean(THEORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_input_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        theoryInput = view.findViewById(R.id.switchFragmentInputStudentTheory);

        theoryInput.setChecked(theory);

        theoryInput.setOnCheckedChangeListener((buttonView, isChecked) -> theory = isChecked);
    }

    public boolean isTheory()
    {
        return theory;
    }

    protected InputStudentFragment(Parcel in)
    {
        theory = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeByte((byte) (theory ? 1 : 0));
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
