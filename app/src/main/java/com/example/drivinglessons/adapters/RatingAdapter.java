package com.example.drivinglessons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Rating;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Locale;

public class RatingAdapter extends FirebaseRecyclerAdapter<Rating, RatingAdapter.ViewHolder>
{
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name, rating, date, message;
        public ImageView image;
        public ViewHolder(@NonNull View itemview)
        {
            super(itemview);
            name = itemview.findViewById(R.id.textViewAdapterRatingName);
            rating = itemview.findViewById(R.id.textViewAdapterRatingRating);
            date = itemview.findViewById(R.id.textViewAdapterRatingDate);
            message = itemview.findViewById(R.id.textViewAdapterRatingMessage);
            image = itemview.findViewById(R.id.imageViewAdapterRating);
        }
    }

    private FirebaseManager fm;
    private Context c;

    public RatingAdapter(FirebaseRecyclerOptions<Rating> options)
    {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType)
    {
        c = parent.getContext();
        fm = FirebaseManager.getInstance(c);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rating, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Rating rating)
    {
        Glide.with(c).load(fm.getStorageReference(rating.fromImage)).circleCrop()
                .thumbnail(Glide.with(c).load(R.drawable.loading).circleCrop())
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.image);

        holder.name.setText(rating.fromName);
        holder.rating.setText(String.format(Locale.ROOT, "%.1f", rating.rate));
        holder.date.setText(Constants.DATE_FORMAT.format(rating.date));
        holder.message.setText(rating.message);

        setVisibility(holder, isFiltered(rating));
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

    private boolean isFiltered(@NonNull Rating rating)
    {
        return true;
    }
}
