package com.example.drivinglessons.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.dialogs.LessonFiltersDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

public class LessonViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "lessons", ID = "id", SEARCH = "search", LESSON_FILTERS = "lesson filters";

    private String id, search;
    private LessonFiltersDialogFragment lessonFilters;

    private ImageView filters;
    private TextInputLayout searchInputLayout;
    private EditText searchInput;

    public LessonViewFragment() {}

    @NonNull
    public static LessonViewFragment newInstance()
    {
        return newInstance(false);
    }

    @NonNull
    public static LessonViewFragment newInstance(boolean isOwner)
    {
        return newInstance(LessonFiltersDialogFragment.newInstance(isOwner));
    }

    @NonNull
    public static LessonViewFragment newInstance(LessonFiltersDialogFragment lessonFilters)
    {
        return newInstance("","", lessonFilters);
    }
    @NonNull
    public static LessonViewFragment newInstance(String id, String search, LessonFiltersDialogFragment lessonFilters)
    {
        LessonViewFragment fragment = new LessonViewFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(SEARCH, search);
        args.putParcelable(LESSON_FILTERS, lessonFilters);
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
            id = args.getString(ID);
            search = args.getString(SEARCH);
            lessonFilters = args.getParcelable(LESSON_FILTERS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_lesson_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        filters = view.findViewById(R.id.imageViewFragmentLessonViewFilters);
        searchInputLayout = view.findViewById(R.id.textInputLayoutFragmentLessonViewSearch);
        searchInput = view.findViewById(R.id.editTextFragmentLessonViewSearch);

        lessonFilters.setCancel(() ->
        {
            lessonFilters.getData();
        });

        filters.setOnClickListener(v -> lessonFilters.show(getChildFragmentManager(), null));
    }

    protected LessonViewFragment(@NonNull Parcel in)
    {
        id = in.readString();
        search = in.readString();
        lessonFilters = in.readParcelable(LessonFiltersDialogFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(search);
        dest.writeParcelable(lessonFilters, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<LessonViewFragment> CREATOR = new Creator<LessonViewFragment>()
    {
        @Override
        public LessonViewFragment createFromParcel(Parcel in)
        {
            return new LessonViewFragment(in);
        }

        @Override
        public LessonViewFragment[] newArray(int size)
        {
            return new LessonViewFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
