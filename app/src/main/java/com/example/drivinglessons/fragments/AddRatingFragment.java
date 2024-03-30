package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.dialogs.AddRatingDialogFragment;


public class AddRatingFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "add rating", IS_STUDENT = "is student", TO_ID = "to id", FROM_ID = "from id";

    private AddRatingDialogFragment dialog;

    private String toId, fromId;
    private boolean isStudent;
    private double rate;

    private TextView title, addRating;
    private RatingBar ratingInput;


    public AddRatingFragment() {}

    @NonNull
    public static AddRatingFragment newInstance(String toId, String fromId, boolean isStudent)
    {
        AddRatingFragment fragment = new AddRatingFragment();

        Bundle args = new Bundle();
        args.putString(TO_ID, toId);
        args.putString(FROM_ID, fromId);
        args.putBoolean(IS_STUDENT, isStudent);
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
            toId = args.getString(TO_ID);
            fromId = args.getString(FROM_ID);
            isStudent = args.getBoolean(IS_STUDENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_add_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.textViewFragmentAddRatingTitle);
        addRating = view.findViewById(R.id.textViewFragmentAddRating);
        ratingInput = view.findViewById(R.id.ratingBarFragmentAddRating);

        title.setText(isStudent ? R.string.add_student_rating : R.string.add_teacher_rating);
        addRating.setOnClickListener(v ->
        {
            dialog = AddRatingDialogFragment.newInstance(toId, fromId, isStudent);
            dialog.reset(rate);
            dialog.show(getChildFragmentManager(), null);
        });
        ratingInput.setOnRatingBarChangeListener((ratingBar, v, b) ->
        {
            if (b)
            {
                rate = v;
                dialog = AddRatingDialogFragment.newInstance(toId, fromId, isStudent);
                dialog.reset(rate);
                dialog.show(getChildFragmentManager(), null);
                ratingInput.setRating(0);
            }
        });
    }

    protected AddRatingFragment(@NonNull Parcel in)
    {
        dialog = in.readParcelable(AddRatingDialogFragment.class.getClassLoader());
        isStudent = in.readByte() == 1;
        rate = in.readDouble();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeParcelable(dialog, flags);
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeDouble(rate);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<AddRatingFragment> CREATOR = new Creator<AddRatingFragment>()
    {
        @Override
        public AddRatingFragment createFromParcel(Parcel in)
        {
            return new AddRatingFragment(in);
        }

        @Override
        public AddRatingFragment[] newArray(int size)
        {
            return new AddRatingFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
