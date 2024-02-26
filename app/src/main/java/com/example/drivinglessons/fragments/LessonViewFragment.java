package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;

public class LessonViewFragment extends Fragment
{
    private static final String FRAGMENT_TITLE = "lessons", SEARCH = "search";

    private String search;

    @NonNull
    public static LessonViewFragment newInstance()
    {
        return newInstance("");
    }
    @NonNull
    public static LessonViewFragment newInstance(String search)
    {
        LessonViewFragment fragment = new LessonViewFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(SEARCH, search);
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
            search = args.getString(SEARCH);
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
    }

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
