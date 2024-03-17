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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.util.dialogs.DialogCancel;

public class TeacherFiltersDialogFragment extends DialogFragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "teacher filters", DATA = "data", IS_OWNER = "is owner";

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
    private DialogCancel cancel;
    private Data data;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch manualInput, testerInput;
    private TextView testerText;

    public TeacherFiltersDialogFragment() {}

    @NonNull
    public static TeacherFiltersDialogFragment newInstance(boolean isOwner)
    {
        return newInstance(isOwner, new Data());
    }

    @NonNull
    public static TeacherFiltersDialogFragment newInstance(boolean isOwner, boolean isManual, boolean isTester)
    {
        return newInstance(isOwner, new Data(isManual, isTester));
    }

    @NonNull
    public static TeacherFiltersDialogFragment newInstance(boolean isOwner, Data data)
    {
        TeacherFiltersDialogFragment fragment = new TeacherFiltersDialogFragment();

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
        return inflater.inflate(R.layout.dialog_fragment_teacher_filters, container, false);
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

    protected TeacherFiltersDialogFragment(@NonNull Parcel in)
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

    public static final Creator<TeacherFiltersDialogFragment> CREATOR = new Creator<TeacherFiltersDialogFragment>()
    {
        @Override
        public TeacherFiltersDialogFragment createFromParcel(Parcel in)
        {
            return new TeacherFiltersDialogFragment(in);
        }

        @Override
        public TeacherFiltersDialogFragment[] newArray(int size)
        {
            return new TeacherFiltersDialogFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
