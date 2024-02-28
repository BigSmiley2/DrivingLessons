package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.dialogs.LessonFiltersDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

public class LessonViewFragment extends Fragment
{
    private static final String FRAGMENT_TITLE = "lessons", ID = "id", SEARCH = "search", LESSON_FILTERS = "lesson filters";

    private String id, search;
    private LessonFiltersDialogFragment lessonFilters;

    private ImageView filters;
    private TextInputLayout searchInputLayout;
    private EditText searchInput;

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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        filters = view.findViewById(R.id.imageViewFragmentLessonsViewFilters);
        searchInputLayout = view.findViewById(R.id.textInputLayoutFragmentLessonsViewSearch);
        searchInput = view.findViewById(R.id.editTextFragmentLessonsViewSearch);

        filters.setOnClickListener(v ->
        {
            lessonFilters.show();
        });
    }

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
