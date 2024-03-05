package com.example.drivinglessons.fragments.input;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.util.validation.TextListener;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.regex.Pattern;

public class InputTeacherFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "teacher", DATA = "data";

    public static class Data implements Parcelable
    {
        public boolean manual, tester;
        public Double cost;

        public Data(boolean manual, boolean tester, Double cost)
        {
            this.manual = manual;
            this.tester = tester;
            this.cost = cost;
        }

        public Data()
        {
            this(false, false, null);
        }

        public Data(@NonNull Data data)
        {
            this(data.manual, data.tester, data.cost);
        }

        protected Data(@NonNull Parcel in)
        {
            manual = in.readByte() == 1;
            tester = in.readByte() == 1;
            cost = in.readByte() == 0 ? null : in.readDouble();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags)
        {
            dest.writeByte((byte) (manual ? 1 : 0));
            dest.writeByte((byte) (tester ? 1 : 0));
            if (cost == null)
                dest.writeByte((byte) 0);
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

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch manualInput, testerInput;
    private TextInputLayout costInputLayout;
    private EditText costInput;

    public InputTeacherFragment() {}

    @NonNull
    public static InputTeacherFragment newInstance()
    {
        return newInstance(new Data());
    }

    @NonNull
    public static InputTeacherFragment newInstance(boolean manual, boolean tester, Double cost)
    {
        return newInstance(new Data(manual, tester, cost));
    }

    @NonNull
    public static InputTeacherFragment newInstance(Data data)
    {
        InputTeacherFragment fragment = new InputTeacherFragment();

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
        return inflater.inflate(R.layout.fragment_input_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        manualInput = view.findViewById(R.id.switchFragmentInputTeacherManual);
        testerInput = view.findViewById(R.id.switchFragmentInputTeacherTester);
        costInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputTeacherCost);
        costInput = view.findViewById(R.id.editTextFragmentInputTeacherCost);

        manualInput.setChecked(data.manual);
        testerInput.setChecked(data.tester);
        if (data.cost != null) costInput.setText(String.format(Locale.ROOT,"%.2f", data.cost));

        manualInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.manual = isChecked);
        testerInput.setOnCheckedChangeListener((buttonView, isChecked) -> data.tester = isChecked);
        costInput.addTextChangedListener((TextListener) s ->
        {
            String str = costInput.getText().toString();
            if (str.isEmpty())
            {
                costInputLayout.setError(null);
                data.cost = null;
            }
            else if (str.length() > 10)
            {
                costInputLayout.setError("cost must contain at most 10 characters");
                data.cost = null;
            }
            else if (Pattern.matches("[0-9]*(\\.[0-9]+)*", str))
            {
                data.cost = Double.parseDouble(str);
                costInputLayout.setError(null);
            }
            else
            {
                costInputLayout.setError("the lesson cost must be a real number");
                data.cost = null;
            }
        });
    }

    public Data getData()
    {
        return data == null ? null : new Data(data);
    }

    public void addCostError(String error)
    {
        if (costInputLayout.getError() == null) costInputLayout.setError(error);
    }

    protected InputTeacherFragment(@NonNull Parcel in)
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
