package com.example.drivinglessons.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Transaction;
import com.example.drivinglessons.util.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Locale;

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

            from = itemview.findViewById(R.id.textViewAdapterTransactionFrom);
            fromLayout = itemview.findViewById(R.id.linearLayoutAdapterTransactionFrom);
            to = itemview.findViewById(R.id.textViewAdapterTransactionTo);
            toLayout = itemview.findViewById(R.id.linearLayoutAdapterTransactionTo);
            date = itemview.findViewById(R.id.textViewAdapterTransactionDate);
            amount = itemview.findViewById(R.id.textViewAdapterTransactionAmount);
            confirmed = itemview.findViewById(R.id.imageViewAdapterTransactionConfirmed);
        }
    }

    private final String id;

    public TransactionAdapter(FirebaseRecyclerOptions<Transaction> options, String id)
    {
        super(options);
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transaction, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Transaction transaction)
    {
        holder.from.setText(transaction.fromName);
        holder.to.setText(transaction.toName);
        holder.date.setText(Constants.DATE_FORMAT.format(transaction.date));
        holder.amount.setText(String.format(Locale.ROOT, "%.2f₪", transaction.amount));

        setVisibility(holder, isFiltered(transaction));
    }

    private void setVisibility(ViewHolder holder, boolean visible)
    {
        if (visible)
        {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else
        {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    private boolean isFiltered(@NonNull Transaction transaction)
    {
        return id.equals(transaction.fromId) || id.equals(transaction.toId);
    }

}
