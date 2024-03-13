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
import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.util.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LessonAdapter extends FirebaseRecyclerAdapter<Lesson, LessonAdapter.ViewHolder>
{
    public interface Runnable
    {
        void run(ViewHolder viewHolder, final int position, Lesson lesson);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title, start, end, options, student, teacher;
        public LinearLayout studentLayout, teacherLayout;
        public ImageView confirmed;
        public View line;

        public ViewHolder(@NonNull View itemview)
        {
            super(itemview);

            title = itemview.findViewById(R.id.textViewAdapterLessonTitle);
            start = itemview.findViewById(R.id.textViewAdapterLessonStartDate);
            end = itemview.findViewById(R.id.textViewAdapterLessonEndDate);
            studentLayout = itemview.findViewById(R.id.linearLayoutAdapterLessonStudent);
            teacherLayout = itemview.findViewById(R.id.linearLayoutAdapterLessonTeacher);
            student = itemview.findViewById(R.id.textViewAdapterLessonStudent);
            teacher = itemview.findViewById(R.id.textViewAdapterLessonTeacher);
            confirmed = itemview.findViewById(R.id.imageViewAdapterLessonConfirmed);
            options = itemview.findViewById(R.id.textViewAdapterLessonOptions);
            line = itemview.findViewById(R.id.viewAdapterLessonLine);
        }
    }

    private final Runnable onOptionsClick;
    private final boolean isAdmin;
    private boolean isConfirmed, isPast, isAssigned;
    private String name;

    public LessonAdapter(FirebaseRecyclerOptions<Lesson> options, boolean isConfirmed, boolean isPast, boolean isAssigned, boolean isAdmin, String name, Runnable onOptionsClick)
    {
        super(options);
        this.isConfirmed = isConfirmed;
        this.isPast = isPast;
        this.isAssigned = isAssigned;
        this.isAdmin = isAdmin;
        this.name = name;
        this.onOptionsClick = onOptionsClick;
    }

    public LessonAdapter(FirebaseRecyclerOptions<Lesson> options, Runnable onOptionsClick)
    {
        this(options, false, false, false, false, "", onOptionsClick);
    }

    public LessonAdapter(FirebaseRecyclerOptions<Lesson> options, @NonNull LessonAdapter other)
    {
        this(options, other.isConfirmed, other.isPast, other.isAssigned, other.isAdmin, other.name, other.onOptionsClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lesson, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Lesson lesson)
    {
        holder.title.setText(lesson.isTest ? R.string.driving_test : R.string.driving_lesson);
        holder.start.setText(toString(lesson.start));
        holder.end.setText(toString(lesson.end));
        holder.student.setText(lesson.studentName);
        holder.teacher.setText(lesson.teacherName == null ? "?" : lesson.teacherName);
        holder.confirmed.setImageResource(lesson.isConfirmed ? R.drawable.check_colored : R.drawable.uncheck_colored);
        holder.options.setOnClickListener(v -> onOptionsClick.run(holder, holder.getAbsoluteAdapterPosition(), lesson));

        setVisibility(holder, isFiltered(lesson));
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
