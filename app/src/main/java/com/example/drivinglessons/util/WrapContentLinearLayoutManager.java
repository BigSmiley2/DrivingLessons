package com.example.drivinglessons.util;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WrapContentLinearLayoutManager extends LinearLayoutManager
{
    public WrapContentLinearLayoutManager(Context context)
    {
        super(context);
    }
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        try { super.onLayoutChildren(recycler, state); }
        catch (IndexOutOfBoundsException e) { e.printStackTrace(); }
    }

    @Override
    public boolean supportsPredictiveItemAnimations()
    {
        return false;
    }
}