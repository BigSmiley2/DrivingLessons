package com.example.drivinglessons.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.firebase.entities.Transaction;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class TransactionAdapter extends FirebaseRecyclerAdapter<Transaction, TransactionAdapter.ViewHolder>
{
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView from, to, date, amount;
        public LinearLayout fromLayout, toLayout;
        public ImageView confirmed;
        public View line;

        public ViewHolder(@NonNull View itemview)
        {
            super(itemview);
        }
    }
}
