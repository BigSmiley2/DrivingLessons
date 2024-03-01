package com.example.drivinglessons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        public TextView name, email, date, info;
        public ImageView imageView;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemview)
        {
            super(itemview);

            imageView = itemview.findViewById(R.id.imageView);

            layout = itemview.findViewById(R.id.constraintLayout);

            name = itemview.findViewById(R.id.textViewName);
            email = itemview.findViewById(R.id.textViewEmail);
            date = itemview.findViewById(R.id.textViewDate);
            info = itemview.findViewById(R.id.textViewInfo);
        }
    }

    private FirebaseManager fm;
    private Context c;
    private final Runnable onClick, onLongClick;
    private boolean hasManual, isTester, hasTheory;
    private String name;
    public UserAdapter(FirebaseRecyclerOptions<User> options, Runnable onClick, Runnable onLongClick, boolean hasManual, boolean isTester, boolean hasTheory)
    {
        super(options);
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        this.hasManual = hasManual;
        this.isTester = isTester;
        this.hasTheory = hasTheory;
        this.name = "";
    }

    public UserAdapter(FirebaseRecyclerOptions<User> options, Runnable onClick, Runnable onLongClick)
    {
        this(options, onClick, onLongClick, false, false, false);
    }
    public UserAdapter(FirebaseRecyclerOptions<User> options, @NonNull UserAdapter adapter)
    {
        this(options, adapter.onClick, adapter.onLongClick, adapter.hasManual, adapter.isTester, adapter.hasTheory);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        c = parent.getContext();
        fm = FirebaseManager.getInstance(c);
        ViewHolder holder = new ViewHolder(LayoutInflater.from(c).inflate(R.layout.adapter_user, parent, false));
        holder.layout.setOnClickListener(v -> onClick.run(holder, holder.getAbsoluteAdapterPosition(), this.getItem(holder.getAbsoluteAdapterPosition())));
        holder.layout.setOnLongClickListener(v ->
        {
            onLongClick.run(holder, holder.getAbsoluteAdapterPosition(), this.getItem(holder.getAbsoluteAdapterPosition()));
            return true;
        });
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull User user)
    {
        Glide.with(c).load(fm.getStorageReference(user.imagePath))
                .thumbnail(Glide.with(c).load(R.drawable.loading).circleCrop())
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imageView);

        holder.name.setText(user.name);
        holder.email.setText(user.email);
        holder.date.setText(Constants.DATE_FORMAT.format(user.birthdate));

        if (user instanceof Teacher)
        {
            Teacher teacher = (Teacher) user;

            setVisibility(holder, !(teacher.hasManual != hasManual || teacher.isTester != isTester || !user.name.contains(name)));
        }
        else if (user instanceof Student)
        {
            Student student = (Student) user;

            setVisibility(holder, !(student.hasTheoryTest != hasTheory || !user.name.contains(name)));
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

    public void setHasManual(boolean hasManual)
    {
        this.hasManual = hasManual;
    }

    public void setHasTheory(boolean hasTheory)
    {
        this.hasTheory = hasTheory;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
