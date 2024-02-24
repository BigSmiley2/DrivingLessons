package com.example.drivinglessons.util.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class PagerAdapter <T extends Fragment> extends FragmentStateAdapter
{
    private final List<T> fragments;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, List<T> fragments)
    {
        super(fragmentActivity);

        this.fragments = fragments;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getItemCount()
    {
        return fragments.size();
    }
}
