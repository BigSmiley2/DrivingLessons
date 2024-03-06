package com.example.drivinglessons.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.util.dialogs.DialogCancel;

public class StudentFiltersDialogFragment extends DialogFragment implements Parcelable
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

    private DialogCancel cancel;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch theoryInput;

    public StudentFiltersDialogFragment() {}

    @NonNull
    public static StudentFiltersDialogFragment newInstance()
    {
        return newInstance(new Data());
    }

    @NonNull
    public static StudentFiltersDialogFragment newInstance(boolean isTheory)
    {
        return newInstance(new Data(isTheory));
    }

    @NonNull
    public static StudentFiltersDialogFragment newInstance(Data data)
    {
        StudentFiltersDialogFragment fragment = new StudentFiltersDialogFragment();

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
        return inflater.inflate(R.layout.dialog_fragment_student_filters, container, false);
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
        return data == null ? null : new Data(data);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null)
        {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            //window.setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        return dialog;
    }

    public void setCancel(DialogCancel cancel)
    {
        this.cancel = cancel;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog)
    {
        super.onCancel(dialog);
        if (cancel != null) cancel.cancel();
    }

    protected StudentFiltersDialogFragment(@NonNull Parcel in)
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

    public static final Creator<StudentFiltersDialogFragment> CREATOR = new Creator<StudentFiltersDialogFragment>()
    {
        @Override
        public StudentFiltersDialogFragment createFromParcel(Parcel in)
        {
            return new StudentFiltersDialogFragment(in);
        }

        @Override
        public StudentFiltersDialogFragment[] newArray(int size)
        {
            return new StudentFiltersDialogFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return TITLE;
    }
}
