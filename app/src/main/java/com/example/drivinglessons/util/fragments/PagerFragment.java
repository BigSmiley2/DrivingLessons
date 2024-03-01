package com.example.drivinglessons.util.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.drivinglessons.R;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class PagerFragment <T extends Fragment & Parcelable> extends Fragment
{
    private static final String IS_SWIPEABLE = "is swipeable", FRAGMENTS = "fragments";
    private ViewPager2 viewPager;
    private TabLayoutMediator tabLayoutMediator;
    private ArrayList<T> fragments;
    private boolean isSwipeable;

    public PagerFragment() {}
    @NonNull
    public static <T extends Fragment & Parcelable> PagerFragment<T> newInstance(ArrayList<T> fragments)
    {
     return newInstance(fragments, true);
    }

    @NonNull
    public static <T extends Fragment & Parcelable> PagerFragment<T> newInstance(ArrayList<T> fragments, boolean isSwipeable)
    {
        PagerFragment<T> fragment = new PagerFragment<>();

        /* saving data state */
        Bundle args = new Bundle();
        args.putBoolean(IS_SWIPEABLE, isSwipeable);
        args.putParcelableArrayList(FRAGMENTS, fragments);
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
            isSwipeable = args.getBoolean(IS_SWIPEABLE);
            fragments = args.getParcelableArrayList(FRAGMENTS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);

        viewPager = view.findViewById(R.id.viewPagerPagerFragment);
        tabLayoutMediator = new TabLayoutMediator(view.findViewById(R.id.tabLayoutPagerFragment), viewPager, (tab, position) -> tab.setText(getFragmentTitle(position)));

        viewPager.setUserInputEnabled(isSwipeable);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewPager.setAdapter(new PagerAdapter<>(requireActivity(), fragments));
        tabLayoutMediator.attach();
    }

    @NonNull
    private String getFragmentTitle(int position)
    {
        return fragments.get(position).toString();
    }

    public void setSwipeable(boolean isSwipeable)
    {
        viewPager.setUserInputEnabled(this.isSwipeable = isSwipeable);
    }
}
