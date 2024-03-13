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

public class LessonFiltersDialogFragment extends DialogFragment implements Parcelable
{
    private static final String TITLE = "lesson filters", DATA = "data", IS_OWNER = "is owner";

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

        public Data(@NonNull Data data)
        {
            this(data.isConfirmed, data.isPast, data.isAssigned);
        }


        protected Data(@NonNull Parcel in)
        {
            isConfirmed = in.readByte() == 1;
            isPast = in.readByte() == 1;
            isAssigned = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags)
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

    private boolean isOwner;
    private Data data;
    private DialogCancel cancel;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch confirmInput, pastInput, assignedInput;
    private TextView assignedText;

    public LessonFiltersDialogFragment() {}

    @NonNull
    public static LessonFiltersDialogFragment newInstance(boolean isOwner)
    {
        return newInstance(isOwner, new Data());
    }

    @NonNull
    public static LessonFiltersDialogFragment newInstance(boolean isOwner, boolean isConfirmed, boolean isPast, boolean isAssigned)
    {
        return newInstance(isOwner, new Data(isConfirmed, isPast, isAssigned));
    }

    @NonNull
    public static LessonFiltersDialogFragment newInstance(boolean isOwner, Data data)
    {
        LessonFiltersDialogFragment fragment = new LessonFiltersDialogFragment();

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
        data = new Data();
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
        return inflater.inflate(R.layout.dialog_fragment_lesson_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        confirmInput = view.findViewById(R.id.switchFragmentLessonFiltersConfirmed);
        pastInput = view.findViewById(R.id.switchFragmentLessonFiltersPast);
        assignedInput = view.findViewById(R.id.switchFragmentLessonFiltersAssigned);
        assignedText = view.findViewById(R.id.textViewFragmentLessonFiltersAssigned);

        if (isOwner)
        {
            assignedInput.setVisibility(View.VISIBLE);
            assignedText.setVisibility(View.VISIBLE);
        }

        confirmInput.setChecked(data.isConfirmed);
        pastInput.setChecked(data.isPast);
        assignedInput.setChecked(data.isAssigned);

        confirmInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isConfirmed = isChecked);
        pastInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isPast = isChecked);
        assignedInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.isAssigned = isChecked);
    }

    public Data getData()
    {
        return data == null ? null : new Data(data);
    }

    public void setCancel(DialogCancel cancel)
    {
        this.cancel = cancel;
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

    @Override
    public void onCancel(@NonNull DialogInterface dialog)
    {
        super.onCancel(dialog);
        if (cancel != null) cancel.cancel();
    }

    protected LessonFiltersDialogFragment(@NonNull Parcel in)
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
