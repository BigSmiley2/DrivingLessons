package com.example.drivinglessons.util.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;

public class HolderFragment<T extends Fragment & Parcelable> extends Fragment
{
    private static final String FRAGMENT_TITLE = "holder fragment", NESTED_FRAGMENT = "nested fragment";
    private T nestedFragment;

    public static <T extends Fragment & Parcelable> HolderFragment<T> newInstance()
    {
        return newInstance(null);
    }

    public static <T extends Fragment & Parcelable> HolderFragment<T> newInstance(T nestedFragment)
    {
        HolderFragment<T> fragment = new HolderFragment<>();

        /* saving data state */
        Bundle args = new Bundle();
        args.putParcelable(NESTED_FRAGMENT, nestedFragment);
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
            nestedFragment = args.getParcelable(NESTED_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_holder, container, false);

        if (nestedFragment != null) replaceFragment(nestedFragment);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private void replaceFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentHolder, fragment).commit();
    }

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
