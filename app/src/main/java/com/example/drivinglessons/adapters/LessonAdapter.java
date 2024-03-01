package com.example.drivinglessons.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.util.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LessonAdapter extends FirebaseRecyclerAdapter<Lesson, LessonAdapter.ViewHolder>
{
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title, start, end, info;
        public View line;

        public ViewHolder(@NonNull View itemview)
        {
            super(itemview);

            title = itemview.findViewById(R.id.textViewAdapterLessonTitle);
            start = itemview.findViewById(R.id.textViewAdapterLessonStartDate);
            end = itemview.findViewById(R.id.textViewAdapterLessonEndDate);
            info = itemview.findViewById(R.id.textViewAdapterLessonInfo);
            line = itemview.findViewById(R.id.viewAdapterLessonLine);
        }
    }
    private boolean isAdmin, isConfirmed, isPast, isAssigned;
    private String name;

    public LessonAdapter(FirebaseRecyclerOptions<Lesson> options, boolean isConfirmed, boolean isPast, boolean isAssigned, boolean isAdmin, String name)
    {
        super(options);
        this.isPast = isPast;
        this.isConfirmed = isConfirmed;
        this.isAssigned = isAssigned;
        this.isAdmin = isAdmin;
        this.name = name;
    }

    public LessonAdapter(FirebaseRecyclerOptions<Lesson> options)
    {
        this(options, false, false, false, false, "");
    }

    public LessonAdapter(FirebaseRecyclerOptions<Lesson> options, @NonNull LessonAdapter adapter)
    {
        this(options, adapter.isConfirmed, adapter.isPast, adapter.isAssigned, adapter.isAdmin, adapter.name);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType)
    {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lesson, parent, false));
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Lesson lesson)
    {
        holder.title.setText(String.format(Locale.ROOT, "Driving %s\n\nwith %s, %s", lesson.isTest ? "test" : "lesson", lesson.studentName, lesson.teacherName == null ? "?": lesson.teacherName));
        holder.start.setText(toString(lesson.start));
        holder.end.setText(toString(lesson.end));
        holder.info.setText(String.format(Locale.ROOT, "cost: %.2f₪", lesson.cost));

        if (isFiltered(lesson))
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

    private boolean isFiltered(Lesson lesson)
    {
        return (!isAdmin || isAssigned == (lesson.teacherId != null)) &&
                lesson.isConfirmed == isConfirmed &&
                (isPast || lesson.start.getTime() > Calendar.getInstance().getTime().getTime()) &&
                (lesson.studentName.contains(name) || lesson.teacherName != null && lesson.teacherName.contains(name));
    }

    @NonNull
    private static String toString(Date date)
    {
        return String.format(Locale.ROOT,"%s\t\t%s", Constants.DATE_FORMAT.format(date), Constants.TIME_FORMAT.format(date));
    }

    public void setPast(boolean isPast)
    {
        this.isPast = isPast;
    }
    public void setConfirmed(boolean isConfirmed)
    {
        this.isConfirmed = isConfirmed;
    }
    public void setAssigned(boolean isAssigned)
    {
        this.isAssigned = isAssigned;
    }
    public void setName(String name)
    {
        this.name = name;
    }
}