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
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder>
{
    public interface Runnable
    {
        void run(ViewHolder viewHolder, final int position, User user);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name, email, date, options;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemview)
        {
            super(itemview);

            imageView = itemview.findViewById(R.id.imageViewAdapterUser);

            name = itemview.findViewById(R.id.textViewAdapterUserFullName);
            email = itemview.findViewById(R.id.textViewAdapterUserEmail);
            date = itemview.findViewById(R.id.textViewAdapterUserDate);
            options = itemview.findViewById(R.id.textViewAdapterUserOptions);
         }
    }

    private FirebaseManager fm;
    private Context c;
    private final Runnable onOptionsClick;
    private boolean isManual, isTester, isTheory;
    private String name;
    public UserAdapter(FirebaseRecyclerOptions<User> options, boolean isManual, boolean isTester, boolean isTheory, String name, Runnable onOptionsClick)
    {
        super(options);
        this.isManual = isManual;
        this.isTester = isTester;
        this.isTheory = isTheory;
        this.name = name;
        this.onOptionsClick = onOptionsClick;
    }

    public UserAdapter(FirebaseRecyclerOptions<User> options, Runnable onOptionsClick)
    {
        this(options, false, false, false, "", onOptionsClick);
    }
    public UserAdapter(FirebaseRecyclerOptions<User> options, @NonNull UserAdapter other)
    {
        this(options, other.isManual, other.isTester, other.isTheory, other.name, other.onOptionsClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        c = parent.getContext();
        fm = FirebaseManager.getInstance(c);
        return new ViewHolder(LayoutInflater.from(c).inflate(R.layout.adapter_user, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull User user)
    {
        Glide.with(c).load(fm.getStorageReference(user.imagePath)).circleCrop()
                .thumbnail(Glide.with(c).load(R.drawable.loading).circleCrop())
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imageView);

        holder.name.setText(user.name);
        holder.email.setText(user.email);
        holder.date.setText(Constants.DATE_FORMAT.format(user.birthdate));
        holder.options.setOnClickListener(v -> onOptionsClick.run(holder, position, user));

        if (user instanceof Teacher)
        {
            Teacher teacher = (Teacher) user;

            setVisibility(holder, !(teacher.hasManual != isManual || teacher.isTester != isTester || !user.name.contains(name)));
        }
        else if (user instanceof Student)
        {
            Student student = (Student) user;

            setVisibility(holder, !(student.hasTheoryTest != isTheory || !user.name.contains(name)));
        }
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

    public void setTester(boolean isTester)
    {
        this.isTester = isTester;
    }

    public void setManual(boolean isManual)
    {
        this.isManual = isManual;
    }

    public void setTheory(boolean isTheory)
    {
        this.isTheory = isTheory;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
